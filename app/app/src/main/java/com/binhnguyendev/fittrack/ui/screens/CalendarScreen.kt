package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.ui.components.ActivityChip
import com.binhnguyendev.fittrack.ui.components.ActivityTag
import com.binhnguyendev.fittrack.ui.components.FtCard
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtPrimaryButton
import com.binhnguyendev.fittrack.ui.components.FtScreen
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtUnderlineInput
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.meta
import com.binhnguyendev.fittrack.ui.vm.CalendarViewModel
import com.binhnguyendev.fittrack.ui.vm.DayCell
import com.binhnguyendev.fittrack.ui.vm.ftViewModel
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CalendarScreen() {
    val vm = ftViewModel { repos -> CalendarViewModel(repos) }
    val state by vm.uiState.collectAsStateWithLifecycle()
    var planning by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        FtScreen {
            Row(Modifier.padding(top = 8.dp, bottom = 4.dp)) {
                FtText(
                    state.monthLabel,
                    color = FT.text,
                    size = 28,
                    family = FontHeadline,
                    letterSpacingEm = -0.05f,
                    lineHeightEm = 1.1f,
                )
                FtText(
                    " ${state.year}",
                    color = FT.text,
                    size = 28,
                    family = FontFine,
                    italic = true,
                    letterSpacingEm = -0.05f,
                    lineHeightEm = 1.1f,
                )
            }

            // Week strip
            Row(
                Modifier.fillMaxWidth().padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                state.week.forEach { d ->
                    WeekDay(d, d.date == state.selected) {
                        vm.select(d.date)
                        planning = false
                    }
                }
            }
            // Activity-chip row (one slot per day)
            Row(
                Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                state.week.forEach { d ->
                    Box(Modifier.width(40.dp), contentAlignment = Alignment.Center) {
                        if (d.kind != null) ActivityChip(d.kind, size = 20.dp)
                        else Box(Modifier.size(20.dp))
                    }
                }
            }

            val selLabel = state.week.firstOrNull { it.date == state.selected }
            val isToday = selLabel?.isToday == true
            FtSectionLabel(
                "${selLabel?.letter ?: ""}, ${state.monthLabel.take(3)} " +
                    "${selLabel?.number ?: ""}${if (isToday) " · Today" else ""}",
            )

            if (state.entries.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .pressScale(0.99f)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, FT.whiteA10, RoundedCornerShape(24.dp))
                        .ftClick { planning = true }
                        .padding(22.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FtIcon("plus", size = 16.dp, color = FT.text2)
                        FtText("Plan workout", color = FT.text2, size = 14)
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    state.entries.forEach { e ->
                        FtCard {
                            ActivityTag(e.kind, e.label)
                            FtText(
                                e.label,
                                modifier = Modifier.padding(top = 8.dp),
                                color = FT.text,
                                size = 24,
                                family = FontHeadline,
                                letterSpacingEm = -0.05f,
                                lineHeightEm = 1.1f,
                            )
                            if (e.done) {
                                Row(
                                    Modifier.padding(top = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    FtIcon("check", size = 12.dp, color = e.kind.meta.color, strokeWidth = 2.4f)
                                    FtText(
                                        "COMPLETED",
                                        color = e.kind.meta.color,
                                        size = 12,
                                        letterSpacingEm = 0.04f,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Plan-workout bottom sheet
        AnimatedVisibility(
            visible = planning,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(FT.scrim)
                    .ftClick { planning = false },
                contentAlignment = Alignment.BottomCenter,
            ) {
                AnimatedVisibility(
                    visible = planning,
                    enter = slideInVertically(tween(400)) { it },
                    exit = slideOutVertically(tween(360)) { it },
                ) {
                    PlanWorkoutSheet(
                        dayLabel = "${selLabelText(state)} ",
                        onCancel = { planning = false },
                        onSave = { kind, label ->
                            vm.addPlanned(kind, label)
                            planning = false
                        },
                    )
                }
            }
        }
    }
}

private fun selLabelText(state: com.binhnguyendev.fittrack.ui.vm.CalendarUiState): String {
    val c = state.week.firstOrNull { it.date == state.selected }
    return "${c?.letter ?: ""}, ${state.monthLabel.take(3)} ${c?.number ?: ""}"
}

@Composable
private fun WeekDay(day: DayCell, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier
            .size(40.dp, 56.dp)
            .pressScale(0.95f)
            .ftClick(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FtText(
            day.letter.uppercase(),
            color = FT.text3,
            size = 10,
            letterSpacingEm = 0.12f,
            lineHeightEm = 1f,
        )
        Box(
            Modifier
                .padding(top = 4.dp)
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selected) FT.orange else androidx.compose.ui.graphics.Color.Transparent),
            contentAlignment = Alignment.Center,
        ) {
            FtText(
                "${day.number}",
                color = if (selected) FT.text else FT.text,
                size = 16,
                family = FontHeadline,
                lineHeightEm = 1f,
            )
        }
    }
}

@Composable
private fun PlanWorkoutSheet(
    dayLabel: String,
    onCancel: () -> Unit,
    onSave: (ActivityKind, String) -> Unit,
) {
    var kind by remember { mutableStateOf(ActivityKind.ROUTINE) }
    var name by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(FT.surface)
            .ftClick { } // swallow taps so the scrim click doesn't dismiss
            .padding(horizontal = 24.dp),
    ) {
        Box(
            Modifier
                .padding(top = 12.dp, bottom = 4.dp)
                .align(Alignment.CenterHorizontally)
                .size(36.dp, 4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(FT.whiteA10),
        )
        Row(
            Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FtText(
                "Plan for $dayLabel",
                modifier = Modifier.weight(1f),
                color = FT.text,
                size = 22,
                family = FontHeadline,
                letterSpacingEm = -0.05f,
            )
            Box(Modifier.size(32.dp).ftClick(onCancel), contentAlignment = Alignment.Center) {
                FtIcon("close", size = 18.dp, color = FT.text2)
            }
        }

        FtSectionLabel("Activity", topPadding = 20.dp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActivityKind.entries.forEach { k ->
                val on = k == kind
                val m = k.meta
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (on) m.soft else androidx.compose.ui.graphics.Color.Transparent)
                        .border(
                            1.dp,
                            if (on) m.color else FT.whiteA05,
                            RoundedCornerShape(16.dp),
                        )
                        .ftClick { kind = k }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    FtIcon(m.icon, size = 18.dp, color = if (on) m.color else FT.text2, strokeWidth = 1.8f)
                    FtText(
                        m.label,
                        color = if (on) m.color else FT.text2,
                        size = 10,
                        letterSpacingEm = 0.04f,
                        lineHeightEm = 1f,
                    )
                }
            }
        }

        FtSectionLabel("Name")
        FtUnderlineInput(
            value = name,
            onValueChange = { name = it },
            placeholder = "Workout name",
            size = 24,
            family = FontHeadline,
        )

        Box(Modifier.weight(1f))

        Row(
            Modifier.fillMaxWidth().padding(bottom = 24.dp),
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
                FtPrimaryButton("Add to ${dayLabel.trim()}") { onSave(kind, name) }
            }
        }
    }
}
