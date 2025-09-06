package com.medprimetech.imageeditor.presentation.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medprimetech.imageeditor.domain.model.FilterSettings
import com.medprimetech.imageeditor.presentation.component.FilterControl
import com.medprimetech.imageeditor.presentation.viewmodel.PhotoEditorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoEditingApp(
    viewModel: PhotoEditorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeFilter by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadBitmap(it, context) }
    }

    val bitmap = viewModel.bitmap
    val filteredBitmap = viewModel.applyFilters()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = { launcher.launch("image/*") },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Select Image")
                }
                if (activeFilter != null) {
                    FloatingActionButton(
                        onClick = {
                            filteredBitmap?.let {
                                viewModel.saveBitmap(context, it)
                                Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Image")
                    }
                    Spacer(modifier = Modifier.height(200.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- Image Preview ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                when {
                    bitmap == null -> {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = Color.Gray
                        )
                    }
                    filteredBitmap != null -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Base image
                            Image(
                                bitmap = filteredBitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .drawBehind {
                                        drawRect(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(Color.Transparent, Color.Black)
                                            )
                                        )
                                    },
                                alignment = Alignment.Center
                            )

                            // Drawing canvas overlay
                            Canvas(
                                modifier = Modifier
                                    .matchParentSize() // ensure it covers the image
                                    .background(Color.Transparent) // keep it see-through
                            ) {
                                // Example: drawing something
                                drawCircle(
                                    color = Color.Red,
                                    radius = 50f,
                                    center = center
                                )

                                // later replace with your DrawingCanvas() composable
                            }
                        }
                    }
                }
            }

            // --- Filter Toolbar ---
            if (bitmap != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E))
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- Active Filter Slider ---
                    activeFilter?.let { filter ->
                        Spacer(Modifier.height(16.dp))
                        val currentSettings = viewModel.filterSettings
                        FilterControl(
                            name = filter,
                            value = when (filter) {
                                "Brightness" -> currentSettings.brightness
                                "Contrast" -> currentSettings.contrast
                                "Exposure" -> currentSettings.exposure
                                "Saturation" -> currentSettings.saturation
                                else -> 0f
                            },
                            min = if (filter == "Contrast" || filter == "Saturation") 0f else -100f,
                            max = if (filter == "Contrast" || filter == "Saturation") 2f else 100f,
                        ) { value ->
                            viewModel.updateFilter(
                                when (filter) {
                                    "Brightness" -> currentSettings.copy(brightness = value)
                                    "Contrast" -> currentSettings.copy(contrast = value)
                                    "Exposure" -> currentSettings.copy(exposure = value)
                                    "Saturation" -> currentSettings.copy(saturation = value)
                                    else -> currentSettings
                                }
                            )
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        val filters = listOf("Brightness", "Contrast", "Saturation", "Exposure")
                        items(filters) { filter ->
                            FilterChip(
                                selected = activeFilter == filter,
                                onClick = { activeFilter = filter },
                                label = { Text(filter, color = Color.White) }
                            )
                        }
                    }
                }
            }
        }
    }
}
