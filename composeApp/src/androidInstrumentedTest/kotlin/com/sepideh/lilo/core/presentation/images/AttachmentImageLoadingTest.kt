package com.sepideh.lilo.core.presentation.images

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.ExifInterface
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.decode.DataSource
import coil3.imageDecoderEnabled
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.request.maxBitmapSize
import coil3.size.Size
import coil3.toBitmap
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

/** Exercises the app's real Coil configuration, Android decoders and ContentResolver. */
@RunWith(AndroidJUnit4::class)
class AttachmentImageLoadingTest {
    private val context: Context get() = ApplicationProvider.getApplicationContext()
    private val loader: ImageLoader get() = SingletonImageLoader.get(context)

    @Test
    fun currentAndLegacyFilesLoadWithCurrentDirectoryTakingPrecedence() = runBlocking {
        val name = "coil-test-${UUID.randomUUID()}.png"
        val current = File(context.filesDir, "images/$name")
        val legacy = File(context.filesDir, "task-images/$name")
        try {
            writeImage(legacy, Color.BLUE)
            val model = AttachmentPreview(name, stored = true)
            assertEquals(Color.BLUE, load(model, cache = false).image.toBitmap().getPixel(0, 0))
            writeImage(current, Color.RED)
            assertEquals(Color.RED, load(model, cache = false).image.toBitmap().getPixel(0, 0))
        } finally { current.delete(); legacy.delete() }
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun selectedContentUriLoadsWithoutCopyingItIntoAttachmentStorage() = runBlocking {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "coil-test-${UUID.randomUUID()}.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        try {
            val bitmap = Bitmap.createBitmap(80, 40, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.GREEN) }
            try {
                requireNotNull(resolver.openOutputStream(uri)).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
            } finally { bitmap.recycle() }
            val result = load(AttachmentPreview(uri.toString(), stored = false))
            assertEquals(Color.GREEN, result.image.toBitmap().getPixel(0, 0))
        } finally { resolver.delete(uri, null, null) }
    }

    @Test
    fun singletonReusesMemoryCacheAndHonorsRequestedDimensions() = runBlocking {
        val file = File(context.filesDir, "images/coil-test-${UUID.randomUUID()}.png")
        try {
            writeImage(file, Color.RED, width = 2400, height = 1200)
            val model = AttachmentPreview(file.name, stored = true)
            assertSame(loader, SingletonImageLoader.get(context))
            val thumbnail = load(model, size = 96)
            assertTrue(thumbnail.image.width <= 96 && thumbnail.image.height <= 96)
            assertEquals(DataSource.MEMORY_CACHE, load(model, size = 96).dataSource)
            val viewer = load(model, size = 1000)
            assertTrue(viewer.image.width > thumbnail.image.width)
            assertTrue(viewer.image.width <= 1000 && viewer.image.height <= 1000)
        } finally { file.delete() }
    }

    @Test
    fun invalidMissingAndCorruptSourcesReturnErrors() = runBlocking {
        val file = File(context.filesDir, "images/coil-test-${UUID.randomUUID()}.jpg")
        try {
            requireNotNull(file.parentFile).mkdirs()
            file.writeText("not an image")
            listOf("../outside.jpg", "coil-missing-${UUID.randomUUID()}.jpg", file.name).forEach { name ->
                assertIs<ErrorResult>(loader.execute(request(AttachmentPreview(name, true), 96, cache = false)))
            }
        } finally { file.delete() }
    }

    @Test
    fun allEightExifOrientationsWorkWithBothAndroidDecoderPaths() = runBlocking {
        // Check rotation AND mirroring using four asymmetric colored corners.
        val expected = listOf(
            listOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW),
            listOf(Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE),
            listOf(Color.YELLOW, Color.BLUE, Color.GREEN, Color.RED),
            listOf(Color.BLUE, Color.YELLOW, Color.RED, Color.GREEN),
            listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW),
            listOf(Color.BLUE, Color.RED, Color.YELLOW, Color.GREEN),
            listOf(Color.YELLOW, Color.GREEN, Color.BLUE, Color.RED),
            listOf(Color.GREEN, Color.YELLOW, Color.RED, Color.BLUE),
        )
        val bitmapFactoryLoader = loader.newBuilder().imageDecoderEnabled(false).build()
        try {
            for (imageLoader in listOf(loader, bitmapFactoryLoader)) {
                for (orientation in 1..8) {
                    val file = File(context.filesDir, "images/coil-test-${UUID.randomUUID()}.jpg")
                    try {
                        writeOrientationImage(file)
                        ExifInterface(file.path).apply {
                            setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString())
                            saveAttributes()
                        }
                        val result = assertIs<SuccessResult>(imageLoader.execute(
                            ImageRequest.Builder(context).data(AttachmentPreview(file.name, true))
                                .size(Size.ORIGINAL).allowHardware(false).memoryCachePolicy(CachePolicy.DISABLED).build()
                        ))
                        val bitmap = result.image.toBitmap()
                        assertEquals(if (orientation >= 5) 40 else 80, bitmap.width)
                        assertEquals(if (orientation >= 5) 80 else 40, bitmap.height)
                        val corners = listOf(
                            bitmap.getPixel(bitmap.width / 4, bitmap.height / 4),
                            bitmap.getPixel(bitmap.width * 3 / 4, bitmap.height / 4),
                            bitmap.getPixel(bitmap.width / 4, bitmap.height * 3 / 4),
                            bitmap.getPixel(bitmap.width * 3 / 4, bitmap.height * 3 / 4),
                        )
                        corners.zip(expected[orientation - 1]).forEach { (actual, wanted) ->
                            assertTrue(kotlin.math.abs(Color.red(actual) - Color.red(wanted)) < 20)
                            assertTrue(kotlin.math.abs(Color.green(actual) - Color.green(wanted)) < 20)
                            assertTrue(kotlin.math.abs(Color.blue(actual) - Color.blue(wanted)) < 20)
                        }
                    } finally { file.delete() }
                }
            }
        } finally { bitmapFactoryLoader.shutdown() }
    }

    private suspend fun load(image: AttachmentPreview, size: Int = 80, cache: Boolean = true): SuccessResult =
        assertIs<SuccessResult>(loader.execute(request(image, size, cache)))

    private fun request(image: AttachmentPreview, size: Int, cache: Boolean) = ImageRequest.Builder(context)
        .data(image).size(size).maxBitmapSize(Size(3200, 3200)).allowHardware(false)
        .memoryCachePolicy(if (cache) CachePolicy.ENABLED else CachePolicy.DISABLED).build()

    private fun writeImage(file: File, color: Int, width: Int = 80, height: Int = 40) {
        requireNotNull(file.parentFile).mkdirs()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }
        try { file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) } }
        finally { bitmap.recycle() }
    }

    private fun writeOrientationImage(file: File) {
        requireNotNull(file.parentFile).mkdirs()
        val bitmap = Bitmap.createBitmap(80, 40, Bitmap.Config.ARGB_8888)
        try {
            for (y in 0 until 40) for (x in 0 until 80) {
                bitmap.setPixel(x, y, when {
                    x < 40 && y < 20 -> Color.RED
                    y < 20 -> Color.GREEN
                    x < 40 -> Color.BLUE
                    else -> Color.YELLOW
                })
            }
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        } finally { bitmap.recycle() }
    }
}
