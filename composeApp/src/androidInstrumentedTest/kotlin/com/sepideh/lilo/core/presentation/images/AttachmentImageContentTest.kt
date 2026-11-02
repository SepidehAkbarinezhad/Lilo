package com.sepideh.lilo.core.presentation.images

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.SingletonImageLoader
import coil3.Uri
import coil3.annotation.DelicateCoilApi
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import com.sepideh.lilo.app.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import okio.Buffer
import okio.FileSystem
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Real AsyncImage composition and rendered pixels; no fake bitmap-loading pipeline. */
@RunWith(AndroidJUnit4::class)
class AttachmentImageContentTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    @Test
    fun thumbnailCropsAndViewerFitsTheSameImage() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val file = File(context.filesDir, "images/coil-ui-${UUID.randomUUID()}.png")
        requireNotNull(file.parentFile).mkdirs()
        file.writeBytes(redImage())
        try {
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                for (thumbnail in listOf(true, false)) {
                    val description = "attachment-$thumbnail"
                    show(scenario, AttachmentPreview(file.name, true), thumbnail, description)
                    val bounds = awaitNode(description)
                    val screenshot = awaitRenderedImage(bounds)
                    try {
                        val center = screenshot.getPixel(bounds.centerX(), bounds.centerY())
                        val top = screenshot.getPixel(bounds.centerX(), bounds.top + bounds.height() / 8)
                        assertTrue(android.graphics.Color.red(center) > 230, "Center: $center, bounds: $bounds")
                        assertTrue(android.graphics.Color.green(center) < 25, "Center: $center")
                        // A 2:1 source fills a square with Crop; Fit leaves black above/below.
                        if (thumbnail) assertTrue(android.graphics.Color.red(top) > 230, "Crop top: $top")
                        else assertTrue(android.graphics.Color.red(top) < 25, "Fit top: $top")
                    } finally { screenshot.recycle() }
                }
            }
        } finally { file.delete() }
    }

    @Test
    @OptIn(DelicateCoilApi::class)
    fun loadingTransitionsToImageAndNewInvalidSourceShowsError() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val original = SingletonImageLoader.get(context)
        val gate = CompletableDeferred<Unit>()
        val bytes = redImage()
        val testLoader = original.newBuilder().components {
            // components { } replaces the configured registry; retain the app's source mapping.
            add(AttachmentPreviewMapper())
            add(Fetcher.Factory<Uri> { data, _, _ ->
                if (data.scheme != "lilo-test") null else object : Fetcher {
                    override suspend fun fetch(): SourceFetchResult {
                        gate.await()
                        return SourceFetchResult(
                            source = ImageSource(Buffer().write(bytes), FileSystem.SYSTEM),
                            mimeType = "image/png",
                            dataSource = DataSource.DISK,
                        )
                    }
                }
            })
        }.build()
        SingletonImageLoader.setUnsafe(testLoader)
        try {
            ActivityScenario.launch(MainActivity::class.java).use { scenario ->
                show(scenario, AttachmentPreview("lilo-test://${UUID.randomUUID()}", false), true, "loaded-image")
                awaitNode("image-loading")
                assertEquals(false, gate.isCompleted)
                gate.complete(Unit)
                awaitNode("loaded-image")
                show(scenario, AttachmentPreview("../invalid.jpg", true), false, "invalid-image")
                awaitNode("image-error")
            }
        } finally {
            gate.cancel()
            SingletonImageLoader.setUnsafe(original)
            testLoader.shutdown()
        }
    }

    private fun show(scenario: ActivityScenario<MainActivity>, image: AttachmentPreview, thumbnail: Boolean, description: String) {
        scenario.onActivity { activity ->
            activity.setContent {
                MaterialTheme {
                    Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
                        AttachmentImageContent(image, thumbnail, description, Modifier.size(120.dp),
                            loading = { Text("image-loading") },
                            error = { Text("image-error") })
                    }
                }
            }
        }
    }

    private fun awaitNode(label: String): Rect {
        val deadline = SystemClock.uptimeMillis() + 10_000
        var hierarchy = ""
        while (SystemClock.uptimeMillis() < deadline) {
            val root = instrumentation.uiAutomation.rootInActiveWindow
            val bounds = root?.let { findBounds(it, label) }
            if (bounds != null && !bounds.isEmpty) return bounds
            hierarchy = root?.let(::describeHierarchy).orEmpty()
            SystemClock.sleep(50)
        }
        error("UI node did not appear: $label; hierarchy: $hierarchy")
    }

    private fun describeHierarchy(node: AccessibilityNodeInfo): String = buildString {
        append("[${node.packageName}: ${node.text} / ${node.contentDescription}]")
        for (index in 0 until node.childCount) node.getChild(index)?.let { append(describeHierarchy(it)) }
    }

    private fun awaitRenderedImage(bounds: Rect): Bitmap {
        // Accessibility reports Success before the render thread necessarily presents that frame.
        val deadline = SystemClock.uptimeMillis() + 5_000
        var center = 0
        while (SystemClock.uptimeMillis() < deadline) {
            instrumentation.waitForIdleSync()
            val screenshot = assertNotNull(instrumentation.uiAutomation.takeScreenshot())
            center = screenshot.getPixel(bounds.centerX(), bounds.centerY())
            if (android.graphics.Color.red(center) > 230 && android.graphics.Color.green(center) < 25) {
                return screenshot
            }
            screenshot.recycle()
            SystemClock.sleep(50)
        }
        error("Image did not render: center=$center, bounds=$bounds")
    }

    private fun findBounds(node: AccessibilityNodeInfo, label: String): Rect? {
        if (node.contentDescription?.toString() == label || node.text?.toString() == label) {
            return Rect().also(node::getBoundsInScreen)
        }
        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            findBounds(child, label)?.let { return it }
        }
        return null
    }

    private fun redImage(): ByteArray {
        val bitmap = Bitmap.createBitmap(240, 120, Bitmap.Config.ARGB_8888).apply {
            eraseColor(android.graphics.Color.RED)
        }
        return try {
            ByteArrayOutputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.toByteArray()
            }
        } finally { bitmap.recycle() }
    }
}
