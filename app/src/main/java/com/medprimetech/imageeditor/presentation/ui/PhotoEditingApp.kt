package com.medprimetech.imageeditor.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
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
    val bitmap by remember { derivedStateOf { viewModel.bitmap } }
    var activeFilter by remember { mutableStateOf<String?>(null) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadBitmap(it, context) }
    }
    // Handle share intent once when app starts
    LaunchedEffect(Unit) {
        val intent = (context as? ComponentActivity)?.intent
        intent?.let { handleIncomingIntent(it, viewModel, context) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = { Text("Image Editor", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                ),
                actions = {
//                    IconButton(onClick = {
//                        launcher.launch("image/*")
//                    }) {
//                        Icon(Icons.Default.Image, contentDescription = "Select Image", tint = Color.White)
//                    }
                    if (bitmap != null) {
                        IconButton(onClick = {
                            viewModel.applyFilters()?.let {
                                viewModel.saveBitmap(context, it)
                                Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Save Image", tint = Color.White)
                        }
                    }
                }
            )
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
                if (bitmap == null) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth(0.5f)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFFFD700), Color(0xFFFF4500)) // Yellow → Red
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select Image",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                } else {
                    viewModel.applyFilters()?.let { filtered ->
                        Image(
                            bitmap = filtered.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            alignment = Alignment.Center
                        )
                    }
                }
            }

            // --- Filter Controls ---
            bitmap?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1C1C1E)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Slider for active filter (toggle visibility)
                    activeFilter?.let { filter ->
                        Spacer(Modifier.height(8.dp))
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
                        Spacer(Modifier.height(6.dp))
                    }

                    // Filter Chips
                    val filters = listOf("Brightness", "Contrast", "Saturation", "Exposure")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(filters) { filter ->
                            FilterChip(
                                selected = activeFilter == filter,
                                onClick = {
                                    activeFilter = if (activeFilter == filter) null else filter
                                },
                                label = {
                                    Text(filter, color = Color.White, fontWeight = FontWeight.Medium)
                                },
                                shape = RoundedCornerShape(50),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.DarkGray,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun handleIncomingIntent(
    intent: Intent,
    viewModel: PhotoEditorViewModel,
    context: Context
) {
    when (intent.action) {
        Intent.ACTION_SEND -> {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            uri?.let { viewModel.loadBitmap(it, context) }
        }
        Intent.ACTION_SEND_MULTIPLE -> {
            val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
            uris?.firstOrNull()?.let { viewModel.loadBitmap(it, context) }
        }
    }
}
