package com.medprimetech.imageeditor.domain.usecases
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import com.medprimetech.imageeditor.domain.model.FilterSettings

class ApplyFilterUseCase {
    operator fun invoke(bitmap: Bitmap, settings: FilterSettings): Bitmap {
        val height = bitmap.height
        val width = bitmap.width
        val bmp = createBitmap(width, height, bitmap.config ?: Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bmp)
        val paint = Paint()

        val colorMatrix = ColorMatrix().apply { setSaturation(settings.saturation) }

        // Brightness
        val brightnessMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, settings.brightness,
                0f, 1f, 0f, 0f, settings.brightness,
                0f, 0f, 1f, 0f, settings.brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )
        colorMatrix.postConcat(brightnessMatrix)

        // Contrast
        val scale = settings.contrast
        val translate = (0.5f * (1 - scale)) * 255
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        colorMatrix.postConcat(contrastMatrix)

        // Exposure
        val exposureMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, settings.exposure,
                0f, 1f, 0f, 0f, settings.exposure,
                0f, 0f, 1f, 0f, settings.exposure,
                0f, 0f, 0f, 1f, 0f
            )
        )
        colorMatrix.postConcat(exposureMatrix)

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return bmp
    }
}