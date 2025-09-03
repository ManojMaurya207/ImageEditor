package com.medprimetech.imageeditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.medprimetech.imageeditor.app.theme.ImageEditorTheme
import com.medprimetech.imageeditor.presentation.ui.PhotoEditingApp
import com.medprimetech.imageeditor.presentation.viewmodel.PhotoEditorViewModel
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageEditorTheme {
                val viewModel: PhotoEditorViewModel = koinViewModel()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    PhotoEditingApp(viewModel, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

