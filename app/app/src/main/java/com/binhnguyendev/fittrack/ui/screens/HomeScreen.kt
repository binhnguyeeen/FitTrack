package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.ui.components.ActivityChip
import com.binhnguyendev.fittrack.ui.components.Avatar
import com.binhnguyendev.fittrack.ui.components.FtCard
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtPrimaryButton
import com.binhnguyendev.fittrack.ui.components.FtScreen
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.Skeleton
import com.binhnguyendev.fittrack.ui.components.StreakBadge
import com.binhnguyendev.fittrack.ui.components.greeting
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.nav.rippleAnchor
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.meta
import com.binhnguyendev.fittrack.ui.vm.HomeViewModel
import com.binhnguyendev.fittrack.ui.vm.RecentItem
import com.binhnguyendev.fittrack.ui.vm.ftViewModel
import androidx.compose.ui.text.font.FontWeight

private val QUICK = listOf(
    ActivityKind.TREADMILL,
    ActivityKind.SWIM,
    ActivityKind.BASKETBALL,
    ActivityKind.ROUTINE,
)

@Composable
fun HomeScreen(
    onOpenProfile: () -> Unit,
    onStartWorkout: (kindKey: String, templateId: Long, center: Offset) -> Unit,
) {
    val vm = ftViewModel { repos -> HomeViewModel(repos) }
    val state by vm.uiState.collectAsStateWithLifecycle()

    FtScreen {
        // Greeting + avatar
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f)) {
                FtText(greeting(), color = FT.text2, size = 15, lineHeightEm = 1.2f)
                FtText(
                    state.name,
                    color = FT.text,
                    size = 38,
                    family = FontFine,
                    italic = true,
                    letterSpacingEm = -0.05f,
                    lineHeightEm = 1.05f,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Avatar(name = state.name, photoUri = state.photoUri, size = 40.dp, onClick = onOpenProfile)
        }

        Box(Modifier.padding(top = 16.dp)) {
            StreakBadge(count = state.streak)
        }

        val today = state.today
        if (today != null) {
            var anchor by remember { mutableStateOf(Offset.Zero) }
            FtSectionLabel("Today")
            FtCard {
                com.binhnguyendev.fittrack.ui.components.ActivityTag(today.kind, today.label)
                FtText(
                    today.label,
                    modifier = Modifier.padding(top = 8.dp),
                    color = FT.text,
                    size = 30,
                    family = FontHeadline,
                    letterSpacingEm = -0.05f,
                    lineHeightEm = 1.05f,
                )
                if (today.exerciseCount != null) {
                    Row(
                        Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        FtText("${today.exerciseCount} exercises", color = FT.text2, size = 13)
                    }
                }
                Box(
                    Modifier
                        .padding(top = 18.dp)
                        .fillMaxWidth()
                        .rippleAnchor { anchor = it },
                ) {
                    FtPrimaryButton("Start", large = false) {
                        onStartWorkout(today.kind.key, today.templateId ?: -1L, anchor)
                    }
                }
            }
        }

        // Quick start
        FtSectionLabel("Quick start")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            QUICK.forEach { kind -> QuickPill(kind, onStartWorkout) }
        }

        // Recent activity
        FtSectionLabel("Recent activity")
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when {
                state.loading -> repeat(3) { RecentSkeleton() }
                state.recent.isEmpty() ->
                    FtText(
                        "No workouts yet — start one above.",
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = FT.text3,
                        size = 13,
                    )
                else -> state.recent.forEach { RecentRow(it) }
            }
        }
    }
}

@Composable
private fun QuickPill(
    kind: ActivityKind,
    onStartWorkout: (String, Long, Offset) -> Unit,
) {
    var anchor by remember { mutableStateOf(Offset.Zero) }
    val m = kind.meta
    Column(
        Modifier
            .size(80.dp, 64.dp)
            .pressScale(0.98f)
            .clip(RoundedCornerShape(20.dp))
            .background(m.soft)
            .rippleAnchor { anchor = it }
            .ftClick { onStartWorkout(kind.key, -1L, anchor) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FtIcon(m.icon, size = 20.dp, color = m.color, strokeWidth = 1.8f)
        FtText(
            m.label,
            modifier = Modifier.padding(top = 4.dp),
            color = FT.text,
            size = 10,
            letterSpacingEm = 0.04f,
            lineHeightEm = 1f,
        )
    }
}

@Composable
private fun RecentRow(item: RecentItem) {
    Row(
        Modifier
            .fillMaxWidth()
            .pressScale(0.98f)
            .padding(horizontal = 4.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ActivityChip(item.kind, size = 36.dp)
        Column(Modifier.weight(1f)) {
            Row {
                FtText(item.title, color = FT.text, size = 14, lineHeightEm = 1.2f)
                FtText(" · ${item.detail}", color = FT.text3, size = 14, lineHeightEm = 1.2f)
            }
            FtText(
                item.whenLabel,
                modifier = Modifier.padding(top = 3.dp),
                color = FT.text2,
                size = 12,
            )
        }
        FtText(item.duration, color = FT.text, size = 14, family = FontFine, italic = true)
    }
}

@Composable
private fun RecentSkeleton() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Skeleton(36.dp, 36.dp, 14.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Skeleton(160.dp, 12.dp, 6.dp)
            Skeleton(90.dp, 10.dp, 5.dp)
        }
        Skeleton(42.dp, 12.dp, 6.dp)
    }
}
