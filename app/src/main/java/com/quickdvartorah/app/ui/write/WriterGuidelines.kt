package com.quickdvartorah.app.ui.write

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun WriterGuidelinesDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Writer Guidelines",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            WriterGuidelinesBody(modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Got it")
            }
        }
    )
}

@Composable
fun WriterGuidelinesBody(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Write a Dvar Torah that can be naturally shared at a Shabbos table.",
            style = MaterialTheme.typography.bodyMedium
        )
        WriterGuidelineLine("Aim for writing that is clear, relatable, rooted in Torah, brief, and focused on one strong idea.")
        WriterGuidelineLine("A strong submission often opens with a question, story, or observation, develops one insight, and ends with a takeaway.")
        WriterGuidelineLine("Prefer a warm, natural style that inspires rather than sounding overly academic.")
        WriterGuidelineLine("Avoid packing in too many points or long source lists without explanation.")
        WriterGuidelineLine("Ask yourself: could this be shared comfortably over a Shabbos table?")
    }
}

@Composable
private fun WriterGuidelineLine(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 2.dp)
    )
}
