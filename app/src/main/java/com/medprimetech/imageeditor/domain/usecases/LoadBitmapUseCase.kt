package com.medprimetech.imageeditor.domain.usecases
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

class LoadBitmapUseCase {
    operator fun invoke(uri: Uri, context: Context): Bitmap? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bitmap = BitmapFactory.decodeStream(inputStream, null, BitmapFactory.Options().apply { inSampleSize = 2 }) ?: return null
        inputStream.close()



        // Re-open to read EXIF
        val exifStream = context.contentResolver.openInputStream(uri)
        val exif = exifStream?.let { ExifInterface(it) }
        exifStream?.close()

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }

        return if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            bitmap
        } else {
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }
    }
}

