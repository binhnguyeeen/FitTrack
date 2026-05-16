package com.binhnguyendev.fittrack.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.binhnguyendev.fittrack.ui.components.Avatar
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtPrimaryButton
import com.binhnguyendev.fittrack.ui.components.FtSegmented
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtToggle
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.vm.SettingsViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel

private val SOCIALS = listOf(
    Triple("twitter", "Twitter", "https://x.com/TrnhQucBnhNguy1"),
    Triple("github", "GitHub", "https://github.com/binhnguyeeen"),
    Triple("youtube", "YouTube", "https://www.youtube.com/@stfcurry"),
    Triple("gmail", "Email", "mailto:trinhquocbinhnguyen@gmail.com"),
)

@Composable
fun SettingsScreen(onBack: () -> Unit, onEditProfile: () -> Unit) {
    val vm = ftViewModel { repos -> SettingsViewModel(repos) }
    val state by vm.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var clearConfirm by remember { mutableStateOf(false) }

    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    fun open(url: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        }
    }

    Box(Modifier.fillMaxSize().background(FT.bg)) {
        Column(Modifier.fillMaxSize().padding(top = statusTop)) {
            Row(
                Modifier.padding(start = 12.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(40.dp).ftClick(onBack),
                    contentAlignment = Alignment.Center,
                ) { FtIcon("arrowL", size = 20.dp, color = FT.text) }
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = navBottom + 32.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .ftClick(onEditProfile)
                        .padding(vertical = 8.dp),
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
                            "Joined 2026",
                            modifier = Modifier.padding(top = 4.dp),
                            color = FT.text2,
                            size = 12,
                        )
                    }
                    FtIcon("edit", size = 14.dp, color = FT.text3)
                }

                GroupLabel("Preferences")
                SRow("Units") {
                    FtSegmented(
                        state.units,
                        listOf("metric" to "Metric", "imperial" to "Imperial"),
                    ) { vm.setUnits(it) }
                }
                SRow("Workout reminders") {
                    FtToggle(state.reminders) { vm.toggleReminders(!state.reminders) }
                }
                SRow("Notifications") {
                    FtToggle(state.notifications) { vm.toggleNotifications(!state.notifications) }
                }
                SRow("24-hour clock") {
                    FtToggle(state.use24h) { vm.setUse24h(!state.use24h) }
                }

                GroupLabel("Data")
                SRow("Export data", value = "CSV", onClick = {})
                SRow("Clear history", onClick = { clearConfirm = true })

                GroupLabel("About")
                SRow("Version", value = "1.0.0")
                SRow("View app on GitHub", onClick = { open("https://github.com/binhnguyeeen") })

                GroupLabel("Connect")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SOCIALS.forEach { (icon, label, url) ->
                        Box(
                            Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(FT.whiteA06)
                                .ftClick { open(url) },
                            contentAlignment = Alignment.Center,
                        ) {
                            FtIcon(icon, size = 20.dp, color = FT.text, strokeWidth = 1.6f)
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = clearConfirm,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(360)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(FT.scrim)
                    .ftClick { clearConfirm = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                AnimatedVisibility(
                    visible = clearConfirm,
                    enter = slideInVertically(tween(400)) { it },
                    exit = slideOutVertically(tween(360)) { it },
                ) {
                    ClearHistorySheet(
                        onCancel = { clearConfirm = false },
                        onConfirm = {
                            vm.clearHistory()
                            clearConfirm = false
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupLabel(text: String) {
    FtText(
        text.uppercase(),
        modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
        color = FT.text3,
        size = 10,
        weight = androidx.compose.ui.text.font.FontWeight.Medium,
        letterSpacingEm = 0.12f,
    )
}

@Composable
private fun SRow(
    label: String,
    value: String? = null,
    onClick: (() -> Unit)? = null,
    right: (@Composable () -> Unit)? = null,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .then(if (onClick != null) Modifier.ftClick(onClick) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FtText(label, modifier = Modifier.weight(1f), color = FT.text, size = 15)
        when {
            right != null -> right()
            value != null -> FtText(value, color = FT.text2, size = 13)
            onClick != null -> FtIcon("arrow", size = 14.dp, color = FT.text3)
        }
    }
}

@Composable
private fun ClearHistorySheet(onCancel: () -> Unit, onConfirm: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(FT.surface)
            .ftClick { }
            .padding(bottom = 4.dp),
    ) {
        Box(
            Modifier
                .padding(top = 12.dp, bottom = 8.dp)
                .align(Alignment.CenterHorizontally)
                .size(36.dp, 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(FT.whiteA10),
        )
        FtText(
            "Clear all history",
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 12.dp),
            color = FT.text,
            size = 22,
            family = FontHeadline,
            letterSpacingEm = -0.05f,
            lineHeightEm = 1.15f,
        )
        FtText(
            "This removes all past sessions and personal bests. It cannot be undone.",
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 10.dp, bottom = 24.dp),
            color = FT.text2,
            size = 14,
            lineHeightEm = 1.5f,
        )
        Row(
            Modifier.padding(start = 24.dp, end = 24.dp, bottom = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FtText(
                "Cancel",
                modifier = Modifier
                    .ftClick(onCancel)
                    .padding(horizontal = 22.dp, vertical = 14.dp),
                color = FT.text2,
                size = 14,
            )
            Box(Modifier.weight(1f)) {
                FtPrimaryButton("Clear history", color = FT.destructive) { onConfirm() }
            }
        }
    }
}
