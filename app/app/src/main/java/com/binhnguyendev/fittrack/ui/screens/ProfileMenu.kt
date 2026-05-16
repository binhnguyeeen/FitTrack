package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.binhnguyendev.fittrack.ui.components.Avatar
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtToggle
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.vm.SettingsViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel

@Composable
fun ProfileMenuOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(360)),
    ) {
        val vm = ftViewModel { repos -> SettingsViewModel(repos) }
        val state by vm.uiState.collectAsStateWithLifecycle()
        val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        Box(
            Modifier
                .fillMaxSize()
                .background(FT.scrim)
                .ftClick(onDismiss),
            contentAlignment = Alignment.BottomCenter,
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(tween(400)) { it },
                exit = slideOutVertically(tween(360)) { it },
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(FT.surface)
                        .ftClick { }
                        .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = navBottom + 28.dp),
                ) {
                    Box(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 18.dp)
                            .size(36.dp, 4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(FT.whiteA10),
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Avatar(state.name, state.photoUri, size = 56.dp)
                        Column(Modifier.weight(1f)) {
                            FtText(
                                state.name,
                                color = FT.text,
                                size = 22,
                                family = FontFine,
                                italic = true,
                                letterSpacingEm = -0.02f,
                                lineHeightEm = 1.1f,
                            )
                            FtText(
                                "${state.streak} consecutive days",
                                modifier = Modifier.padding(top = 4.dp),
                                color = FT.text2,
                                size = 12,
                            )
                        }
                    }

                    FtText(
                        "ACCOUNT",
                        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                        color = FT.text3,
                        size = 10,
                        weight = androidx.compose.ui.text.font.FontWeight.Medium,
                        letterSpacingEm = 0.12f,
                    )
                    MenuRow("Edit profile", onClick = onEditProfile)
                    Row(
                        Modifier.fillMaxWidth().height(52.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        FtText("24-hour clock", modifier = Modifier.weight(1f), color = FT.text, size = 15)
                        FtToggle(state.use24h) { vm.setUse24h(!state.use24h) }
                    }
                    MenuRow("Settings", onClick = onOpenSettings)
                }
            }
        }
    }
}

@Composable
private fun MenuRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .ftClick(onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FtText(label, modifier = Modifier.weight(1f), color = FT.text, size = 15)
        FtIcon("arrow", size = 14.dp, color = FT.text3)
    }
}
