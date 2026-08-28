package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.localization.LocalAppStrings

val ColorPaletteGrid = listOf(
    0xFF4F46E5, // Indigo
    0xFF7C3AED, // Violet
    0xFF9333EA, // Purple
    0xFFC026D3, // Fuchsia
    0xFFDB2777, // Pink
    0xFFE11D48, // Rose
    0xFFEF4444, // Red
    0xFFEA580C, // Orange
    0xFFD97706, // Amber
    0xFFCA8A04, // Yellow
    0xFF65A30D, // Lime
    0xFF16A34A, // Green
    0xFF059669, // Emerald
    0xFF0D9488, // Teal
    0xFF0891B2, // Cyan
    0xFF0284C7, // Sky
    0xFF2563EB, // Royal Blue
    0xFF3B82F6, // Blue
    0xFF475569, // Slate
    0xFF1E293B  // Midnight Dark
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    initialColorHex: Long,
    onColorSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    title: String = "مربع اختيار الألوان"
) {
    val strings = LocalAppStrings.current
    var selectedHex by remember { mutableStateOf(initialColorHex) }
    var hexInputText by remember {
        mutableStateOf(String.format("#%06X", 0xFFFFFF and initialColorHex.toInt()))
    }
    var isInputError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Live Color Box Display
                Box(
                    modifier = Modifier
                        .size(width = 160.dp, height = 56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(selectedHex))
                        .border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hexInputText,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 20-Color Square Matrix
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ColorPaletteGrid.forEach { colorVal ->
                        val isSelected = selectedHex == colorVal
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(colorVal))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) Color.White else Color.Black.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    selectedHex = colorVal
                                    hexInputText = String.format("#%06X", 0xFFFFFF and colorVal.toInt())
                                    isInputError = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manual Hex Code Input Box
                OutlinedTextField(
                    value = hexInputText,
                    onValueChange = { input ->
                        hexInputText = input
                        try {
                            val clean = input.removePrefix("#")
                            if (clean.length == 6) {
                                selectedHex = 0xFF000000 or clean.toLong(16)
                                isInputError = false
                            }
                        } catch (e: Exception) {
                            isInputError = true
                        }
                    },
                    label = { Text("كود اللون Hex (مثال #4F46E5)") },
                    singleLine = true,
                    isError = isInputError,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(strings.cancel)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onColorSelected(selectedHex)
                            onDismiss()
                        }
                    ) {
                        Text(strings.save)
                    }
                }
            }
        }
    }
}
