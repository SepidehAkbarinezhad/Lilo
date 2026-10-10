package com.sepideh.lilo.core.presentation.components.images

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sepideh.lilo.core.domain.images.IMAGE_SELECTION_LIMIT
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.AppBackHandler
import com.sepideh.lilo.core.presentation.components.form.FormSelectionRow
import com.sepideh.lilo.core.presentation.format.localizedDigits
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.core.presentation.images.*
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ImageAttachments(images: List<AttachmentPreview>, accent: Color, enabled: Boolean,
    onAdd: () -> Unit, onRemove: (AttachmentPreview) -> Unit) {
    var viewing by remember { mutableStateOf<String?>(null) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormSelectionRow(LiloIcons.Images, stringResource(Res.string.attachment_images),
            stringResource(Res.string.attachment_count, images.size).localizedDigits(rtl), onAdd,
            enabled = enabled && images.size < IMAGE_SELECTION_LIMIT, accent = accent)
        if (images.isNotEmpty()) LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(images, key = { _, image -> image.key }) { index, image ->
                Box(Modifier.size(96.dp)) {
                    AttachmentImage(image, thumbnail = true, description = stringResource(Res.string.attachment_open, index + 1),
                        modifier = Modifier.fillMaxSize().clip(MaterialTheme.shapes.medium).clickable { viewing = image.key })
                    IconButton(onClick = { onRemove(image) }, enabled = enabled,
                        modifier = Modifier.align(Alignment.TopEnd).size(48.dp)) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = .92f)) {
                            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                Icon(LiloIcons.Close, stringResource(Res.string.attachment_remove, index + 1),
                                    Modifier.size(14.dp), tint = if (enabled) LiloExtendedTheme.colors.textPrimary else LiloExtendedTheme.colors.textDisabled)
                            }
                        }
                    }
                }
            }
        }
        if (images.size >= IMAGE_SELECTION_LIMIT) AppText(text = Res.string.attachment_limit, color = LiloExtendedTheme.colors.textSupporting)

    }
    val index = images.indexOfFirst { it.key == viewing }
    if (index >= 0) ImageViewer(images, index) { viewing = null }
    else if (viewing != null) LaunchedEffect(viewing) { viewing = null }
}

@Composable
private fun AttachmentImage(image: AttachmentPreview, thumbnail: Boolean, description: String, modifier: Modifier) {
    val loaded by rememberAttachmentBitmap(image, thumbnail)
    val bitmap = loaded.bitmap
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.surfaceVariant) {
        if (bitmap != null) Image(bitmap, description, Modifier.fillMaxSize(), contentScale = if (thumbnail) ContentScale.Crop else ContentScale.Fit)
        else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (loaded.loading) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp, color = LiloExtendedTheme.colors.textSupporting)
            else Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(LiloIcons.Images, description, tint = LiloExtendedTheme.colors.textSupporting)
                if (!thumbnail) AppText(text = Res.string.attachment_unavailable, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun ImageViewer(images: List<AttachmentPreview>, initialPage: Int, onClose: () -> Unit) {
    val pager = rememberPagerState(initialPage = initialPage, pageCount = { images.size })
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        AppBackHandler { onClose() }
        Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onClose) { Icon(LiloIcons.Close, stringResource(Res.string.attachment_close)) }
                    AppText(text = stringResource(Res.string.attachment_position, pager.currentPage + 1, images.size).localizedDigits(rtl), modifier = Modifier.weight(1f))
                }
                HorizontalPager(state = pager, modifier = Modifier.weight(1f), key = { images[it].key }) { page ->
                    AttachmentImage(images[page], thumbnail = false, description = stringResource(Res.string.attachment_open, page + 1), modifier = Modifier.fillMaxSize().padding(12.dp))
                }
            }
        }
    }
}
