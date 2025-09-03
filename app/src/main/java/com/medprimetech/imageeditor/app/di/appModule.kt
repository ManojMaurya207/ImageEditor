package com.medprimetech.imageeditor.app.di
import com.medprimetech.imageeditor.domain.usecases.ApplyFilterUseCase
import com.medprimetech.imageeditor.domain.usecases.LoadBitmapUseCase
import com.medprimetech.imageeditor.domain.usecases.SaveImageUseCase
import com.medprimetech.imageeditor.presentation.viewmodel.PhotoEditorViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { ApplyFilterUseCase() }
    single { LoadBitmapUseCase() }
    single { SaveImageUseCase() }
    viewModel { PhotoEditorViewModel(get(), get(), get()) }
}