package com.binhnguyendev.fittrack.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontText

/**
 * Borderless text input with the prototype's animated orange underline (0→100%
 * width on focus). [size] is sp; letter spacing follows the prototype's em
 * values for display inputs.
 */
@Composable
fun FtUnderlineInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    size: Int = 24,
    family: FontFamily = FontText,
    weight: FontWeight = FontWeight.Normal,
    italic: Boolean = false,
    letterSpacingEm: Float = -0.05f,
    color: Color = FT.text,
    underlineColor: Color = FT.orange,
    align: TextAlign = TextAlign.Start,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
) {
    var focused by remember { mutableStateOf(false) }
    val style = TextStyle(
        color = color,
        fontSize = size.sp,
        fontFamily = family,
        fontWeight = weight,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        letterSpacing = (size * letterSpacingEm).sp,
        textAlign = align,
    )
    androidx.compose.foundation.layout.Column(modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                FtText(
                    placeholder,
                    color = FT.text3,
                    size = size,
                    family = family,
                    weight = weight,
                    italic = italic,
                    letterSpacingEm = letterSpacingEm,
                    lineHeightEm = 1.1f,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focused = it.isFocused },
                textStyle = style,
                singleLine = singleLine,
                cursorBrush = SolidColor(underlineColor),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            )
        }
        FtUnderline(focused = focused, color = underlineColor)
    }
}
