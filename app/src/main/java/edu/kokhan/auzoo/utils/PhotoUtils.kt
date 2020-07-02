package edu.kokhan.auzoo.utils

import android.graphics.Bitmap
import android.os.Handler
import android.view.PixelCopy
import android.view.SurfaceView
import io.reactivex.Single
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private fun generateFilename(): String {
    val date = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    return "${getPhotoGalleryPath()}${date}_screenshot.png"
}

private fun saveBitmapToDisk(bitmap: Bitmap): Single<File> =
    Single.create {

        val filename = generateFilename()

        val outFile = File(filename)
        if (!outFile.parentFile.exists()) {
            outFile.parentFile.mkdirs()
        }

        val outputStream = FileOutputStream(outFile)
        val outputData = ByteArrayOutputStream()
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData)
            outputData.writeTo(outputStream)
            outputStream.flush()
        } catch (ex: IOException) {
            throw IOException("Failed to save bitmap to disk", ex)
        } finally {
            outputStream.close()
        }
        it.onSuccess(outFile)
    }

private fun copyPixels(source: SurfaceView): Single<Bitmap> =
    Single.create {

        val bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)

        PixelCopy.request(
            source,
            bitmap,
            { copyResult ->
                if (copyResult == PixelCopy.SUCCESS) {
                    it.onSuccess(bitmap)
                } else {
                    it.onError(Throwable("Failed to copyPixels: $copyResult"))
                }
            },
            Handler()
        )
    }


fun takePhotoFromCamera(sourceView: SurfaceView): Single<File> =
    copyPixels(sourceView)
        .flatMap { saveBitmapToDisk(it) }