package com.binhnguyendev.fittrack.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FT_EASE

@Composable
fun FtToggle(on: Boolean, onChange: () -> Unit) {
    val knob by animateDpAsState(
        targetValue = if (on) 21.dp else 3.dp,
        animationSpec = tween(200, easing = FT_EASE),
        label = "knob",
    )
    Box(
        Modifier
            .size(44.dp, 26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (on) FT.orange else FT.raised)
            .ftClick(onChange),
    ) {
        Box(
            Modifier
                .offset(x = knob, y = 3.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(FT.text),
        )
    }
}

@Composable
fun FtSegmented(value: String, options: List<Pair<String, String>>, onChange: (String) -> Unit) {
    val activeIdx = options.indexOfFirst { it.first == value }.coerceAtLeast(0)
    BoxWithConstraints(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(FT.raised)
            .padding(3.dp),
    ) {
        val slot = maxWidth / options.size
        val x by animateDpAsState(
            targetValue = slot * activeIdx,
            animationSpec = tween(250, easing = FT_EASE),
            label = "seg",
        )
        Box(
            Modifier
                .offset(x = x)
                .size(slot, 26.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(FT.orange),
        )
        Row {
            options.forEach { (k, lbl) ->
                Box(
                    Modifier
                        .size(slot, 26.dp)
                        .ftClick { onChange(k) },
                    contentAlignment = Alignment.Center,
                ) {
                    FtText(
                        lbl,
                        color = if (k == value) FT.text else FT.text2,
                        size = 12,
                    )
                }
            }
        }
    }
}
