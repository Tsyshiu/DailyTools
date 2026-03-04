package com.github.tsyshiu.dailytools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.tsyshiu.dailytools.tools.calculateHash
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.net.URI

@Composable
actual fun PlatformSpecificTools() {
    HashCalculate()
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HashCalculate() {
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var hashResult by remember { mutableStateOf("") }
    var isDragging by remember { mutableStateOf(false) }
    val hashAlgorithms = listOf("MD5", "SHA-256", "SHA-512")
    var selectedAlgorithm by remember { mutableStateOf(hashAlgorithms.first()) }
    val coroutineScope = rememberCoroutineScope()

    val dragAndDropTarget = remember {
        object : DragAndDropTarget {
            override fun onEntered(event: DragAndDropEvent) {
                isDragging = true
            }

            override fun onExited(event: DragAndDropEvent) {
                isDragging = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDragging = false
                when (val dragData = event.dragData()) {
                    is DragData.FilesList -> {
                        val files = dragData.readFiles()
                        // 处理文件
                        if (files.isNotEmpty()) {
                            selectedFile = File(URI(files.first().trim()))
                            return true
                        }
                    }
                    is DragData.Text -> {
                        // val text = dragData.readText()
                        return false
                    }
                    is DragData.Image -> {
                        dragData.readImage()
                        // 处理图片
                    }
                }
                return false
            }
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val borderColor =
            if (isDragging) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(8.dp))
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { event ->
                        event.dragData().let {
                            it is DragData.FilesList
                        }
                    },
                    target = dragAndDropTarget
                )
                .pointerInput(Unit) {
                    detectTapGestures {
                        val dialog = FileDialog(null as Frame?, "Select File")
                        dialog.mode = FileDialog.LOAD
                        dialog.isVisible = true
                        val file = dialog.file
                        val dir = dialog.directory
                        if (file != null && dir != null) {
                            selectedFile = File(dir, file)
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isDragging) "Drop file here" else selectedFile?.path
                    ?: "Drag and drop a file here, or click to select.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                hashAlgorithms.forEach { algorithm ->
                    Row(
                        Modifier
                            .selectable(
                                selected = (algorithm == selectedAlgorithm),
                                onClick = { selectedAlgorithm = algorithm },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (algorithm == selectedAlgorithm),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = algorithm,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    selectedFile?.let {
                        coroutineScope.launch {
                            hashResult = calculateHash(it, selectedAlgorithm)
                        }
                    }
                },
                enabled = selectedFile != null
            ) {
                Text("Calculate")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = hashResult,
            onValueChange = {},
            label = { Text("Hash Result") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
