package com.binhnguyendev.fittrack.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.binhnguyendev.fittrack.ui.components.FtPrimaryButton
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtUnderlineInput
import com.binhnguyendev.fittrack.ui.components.avatarModel
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.nav.rippleAnchor
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FT_EASE
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.FontText
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.vm.OnboardingViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(onComplete: (Offset) -> Unit) {
    val vm = ftViewModel { repos -> OnboardingViewModel(repos) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var pic by remember { mutableStateOf<String?>(null) }
    var anchor by remember { mutableStateOf(Offset.Zero) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            scope.launch {
                pic = com.binhnguyendev.fittrack.ui.util.ImageStorage.persist(context, uri)
            }
        }
    }

    val canContinue = name.trim().length >= 2
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    fun finish() = vm.finish(name, pic) { onComplete(anchor) }

    Column(
        Modifier
            .fillMaxSize()
            .background(FT.bg)
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = statusTop + 64.dp,
                bottom = navBottom + 28.dp,
            ),
    ) {
        // Two-segment progress indicator
        Row(
            Modifier.fillMaxWidth().padding(bottom = 44.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            listOf(1, 2).forEach { seg ->
                val c by animateColorAsState(
                    if (step == seg) FT.orange else FT.whiteA10,
                    tween(300, easing = FT_EASE),
                    label = "seg$seg",
                )
                Box(
                    Modifier
                        .weight(1f)
                        .height(2.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .background(c),
                )
            }
        }

        Column(Modifier.weight(1f)) {
            if (step == 1) {
                Heading("What's your ", "name?", FontHeadline)
                Spacer(Modifier.height(56.dp))
                FtUnderlineInput(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Your name",
                    size = 36,
                    family = FontHeadline,
                    letterSpacingEm = -0.05f,
                )
                FtText(
                    "You can always change this in Settings.",
                    modifier = Modifier.padding(top = 14.dp),
                    color = FT.text2,
                    size = 13,
                    lineHeightEm = 1.4f,
                )
            } else {
                Heading("Add a ", "photo", FontFine)
                Spacer(Modifier.height(56.dp))
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                ) {
                    AvatarPicker(pic) {
                        picker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly,
                            ),
                        )
                    }
                    if (pic != null) {
                        FtText(
                            "Remove photo",
                            modifier = Modifier.ftClick { pic = null },
                            color = FT.text3,
                            size = 12,
                        )
                    }
                    FtText(
                        "You can always change this in Settings.",
                        color = FT.text2,
                        size = 13,
                        lineHeightEm = 1.4f,
                        align = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }

        if (step == 1) {
            FtPrimaryButton(
                "Continue",
                enabled = canContinue,
                onClick = { if (canContinue) step = 2 },
            )
        } else {
            Row(
                Modifier
                    .fillMaxWidth()
                    .rippleAnchor { anchor = it },
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FtText(
                    "Skip",
                    modifier = Modifier
                        .ftClick { finish() }
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    color = FT.text2,
                    size = 14,
                )
                Box(Modifier.weight(1f)) {
                    FtPrimaryButton("Done", onClick = { finish() })
                }
            }
        }
    }
}

@Composable
private fun Heading(prefix: String, emphasis: String, emphasisFamily: androidx.compose.ui.text.font.FontFamily) {
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontFamily = FontText,
                fontWeight = FontWeight.Normal,
                fontSize = 48.sp,
                letterSpacing = (48 * -0.05f).sp,
            ),
        ) { append(prefix) }
        withStyle(
            SpanStyle(
                fontFamily = emphasisFamily,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 48.sp,
                letterSpacing = (48 * -0.05f).sp,
            ),
        ) { append(emphasis) }
    }
    androidx.compose.material3.Text(
        text = text,
        color = FT.text,
        lineHeight = 48.sp,
    )
}

@Composable
private fun AvatarPicker(pic: String?, onPick: () -> Unit) {
    Box(
        Modifier
            .size(120.dp)
            .pressScale(0.97f)
            .clip(CircleShape)
            .background(if (pic != null) FT.black else androidx.compose.ui.graphics.Color.Transparent)
            .then(
                if (pic == null) {
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
        if (pic != null) {
            AsyncImage(
                model = avatarModel(pic),
                contentDescription = "Profile photo",
                modifier = Modifier.size(120.dp).clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            )
        } else {
            FtIcon("camera", size = 20.dp, color = FT.orange, strokeWidth = 1.6f)
        }
    }
}
