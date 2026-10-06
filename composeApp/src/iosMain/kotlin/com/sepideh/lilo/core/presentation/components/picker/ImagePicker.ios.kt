package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.runtime.*
import androidx.compose.ui.uikit.LocalUIViewController
import com.sepideh.lilo.core.domain.images.IMAGE_SELECTION_LIMIT
import com.sepideh.lilo.core.domain.images.ImageSource
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.sepideh.lilo.core.data.images.loadSelectedImage
import com.sepideh.lilo.core.data.images.removeSelectedImageSource
import platform.PhotosUI.*
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePicker(onSelected: (List<ImageSource>) -> Unit, onError: () -> Unit): () -> Unit {
    val selected by rememberUpdatedState(onSelected)
    val error by rememberUpdatedState(onError)
    val host = LocalUIViewController.current
    val scope = rememberCoroutineScope()
    val session = remember(host, scope) { ImagePickerSession(host, scope, { selected(it) }, { error() }) }
    DisposableEffect(session) { onDispose { session.dispose() } }
    return { session.launch() }
}

/** Strongly owns the weak UIKit delegate and all temporary images for the lifetime of the form. */
@OptIn(ExperimentalForeignApi::class)
private class ImagePickerSession(
    private val host: UIViewController,
    private val scope: CoroutineScope,
    private val onSelected: (List<ImageSource>) -> Unit,
    private val onError: () -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol, UIAdaptivePresentationControllerDelegateProtocol {
    private var controller: PHPickerViewController? = null
    private var loading: Job? = null
    private var disposed = false
    private val stagedPaths = mutableListOf<String>()

    fun launch() {
        if (disposed || controller != null || loading?.isActive == true) return
        try {
            val configuration = PHPickerConfiguration().apply {
                filter = PHPickerFilter.imagesFilter()
                selectionLimit = IMAGE_SELECTION_LIMIT.toLong()
            }
            val picker = PHPickerViewController(configuration)
            picker.delegate = this
            var presenter = host
            while (presenter.presentedViewController != null) presenter = requireNotNull(presenter.presentedViewController)
            check(presenter.view.window != null) { "Image picker host is not visible" }
            controller = picker
            presenter.presentViewController(picker, animated = true, completion = null)
            picker.presentationController?.delegate = this
        } catch (_: Exception) {
            controller = null
            onError()
        }
    }

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)
        picker.delegate = null
        controller = null
        if (disposed || didFinishPicking.isEmpty()) return
        loading = scope.launch {
            val batch = mutableListOf<ImageSource>()
            try {
                didFinishPicking.filterIsInstance<PHPickerResult>().take(IMAGE_SELECTION_LIMIT).forEach { result ->
                    batch.add(loadSelectedImage(result.itemProvider))
                }
                // Keep prior staged selections until Save/Dispose; failed selection must not erase them.
                stagedPaths.addAll(batch.map { it.location })
                if (!disposed) onSelected(batch)
            } catch (failure: Throwable) {
                batch.forEach { removeSelectedImageSource(it.location) }
                if (failure is CancellationException) throw failure
                if (!disposed) onError()
            }
        }
    }

    override fun presentationControllerDidDismiss(presentationController: UIPresentationController) {
        controller?.delegate = null
        controller = null
    }

    fun dispose() {
        disposed = true
        loading?.cancel()
        controller?.delegate = null
        controller?.dismissViewControllerAnimated(false, completion = null)
        controller = null
        stagedPaths.forEach(::removeSelectedImageSource)
        stagedPaths.clear()
    }
}

