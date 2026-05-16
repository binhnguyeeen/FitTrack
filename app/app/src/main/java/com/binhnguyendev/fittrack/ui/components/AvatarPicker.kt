package com.binhnguyendev.fittrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.binhnguyendev.fittrack.ui.theme.FT

/** 120dp circular avatar picker — dashed orange ring when empty (prototype
 *  Onboarding step 2 / EditProfile). */
@Composable
fun AvatarPicker(photo: String?, onPick: () -> Unit) {
    Box(
        Modifier
            .size(120.dp)
            .pressScale(0.97f)
            .clip(CircleShape)
            .background(if (photo != null) FT.black else Color.Transparent)
            .then(
                if (photo == null) {
                    Modifier.drawBehind {
                        drawCircle(
                            color = FT.orangeA40,
                            style = Stroke(
                                width = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                            ),
                        )
                    }
                } else {
                    Modifier.border(1.dp, FT.whiteA10, CircleShape)
                },
            )
            .ftClick(onPick),
        contentAlignment = Alignment.Center,
    ) {
        if (photo != null) {
            AsyncImage(
                model = avatarModel(photo),
                contentDescription = "Profile photo",
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            FtIcon("camera", size = 20.dp, color = FT.orange, strokeWidth = 1.6f)
        }
    }
}
