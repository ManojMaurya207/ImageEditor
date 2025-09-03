package com.medprimetech.imageeditor.presentation.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.medprimetech.imageeditor.domain.model.FilterSettings
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
                if (bitmap != null) {
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
                        Image(
                            bitmap = filteredBitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            alignment = Alignment.Center
                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterControl(
    name: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Label + value row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                color = Color(0xFFCCCCCC)
            )
            Text(
                text = "%.2f".format(value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Slider
        Slider(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            valueRange = min..max,
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFF4CAF50),
                inactiveTrackColor = Color(0xFF555555)
            ),
            thumb = {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = null,
                    tint = Color.White
                )
            },
            track = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(0xFF4CAF50))
                )
            },
        )
    }
}
