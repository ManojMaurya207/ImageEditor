package com.medprimetech.imageeditor.domain.usecases
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveImageUseCase {
    suspend operator fun invoke(bitmap: Bitmap, context: Context) {
        withContext(Dispatchers.IO) {
            try {
                val fileName = "edited_image_${System.currentTimeMillis()}.png"
                val contentResolver = context.contentResolver
                val contentValue = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                }
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }
                    contentValue.clear()
                    contentValue.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(it, contentValue, null, null)
                }

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}