package com.medprimetech.imageeditor.presentation.viewmodel
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medprimetech.imageeditor.domain.model.FilterSettings
import com.medprimetech.imageeditor.domain.usecases.ApplyFilterUseCase
import com.medprimetech.imageeditor.domain.usecases.LoadBitmapUseCase
import com.medprimetech.imageeditor.domain.usecases.SaveImageUseCase
import kotlinx.coroutines.launch


class PhotoEditorViewModel(
    private val applyFilterUseCase: ApplyFilterUseCase,
    private val loadBitmapUseCase: LoadBitmapUseCase,
    private val saveImageUseCase: SaveImageUseCase
) : ViewModel() {

    var bitmap by mutableStateOf<Bitmap?>(null)
        private set

    var filterSettings by mutableStateOf(FilterSettings())
        private set

    fun loadBitmap(uri: Uri, context: Context) {
        bitmap = loadBitmapUseCase(uri, context)
        filterSettings = FilterSettings()
    }

    fun applyFilters(): Bitmap? {
        return bitmap?.let { applyFilterUseCase(it, filterSettings) }
    }

    fun saveBitmap(context: Context, filtered: Bitmap) {
        viewModelScope.launch {
            val success = saveImageUseCase(filtered, context)
            // maybe emit a UI event (toast/snackbar)
        }
    }

    fun updateFilter(settings: FilterSettings) {
        filterSettings = settings
    }
}
