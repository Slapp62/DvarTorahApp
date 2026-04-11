package com.example.dvartorahapp.ui.components

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection

private fun isHebrew(text: String): Boolean {
    val first = text.trimStart().firstOrNull() ?: return false
    val dir = Character.getDirectionality(first)
    return dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
           dir == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
}

@Composable
fun RtlAwareText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current
) {
    val direction = if (isHebrew(text)) TextDirection.Rtl else TextDirection.Ltr
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(textDirection = direction)
    )
}
