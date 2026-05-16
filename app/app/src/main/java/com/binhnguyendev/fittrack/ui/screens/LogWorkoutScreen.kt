package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtPrimaryButton
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtUnderlineInput
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.nav.rippleAnchor
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.meta
import com.binhnguyendev.fittrack.ui.vm.SetRow
import com.binhnguyendev.fittrack.ui.vm.WorkoutViewModel

private fun fmt(s: Int): String {
    val h = s / 3600
    val m = (s % 3600) / 60
    val ss = s % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, ss) else "%d:%02d".format(m, ss)
}

@Composable
fun LogWorkoutScreen(
    vm: WorkoutViewModel,
    onFinished: (sessionId: Long, center: Offset) -> Unit,
) {
    val acc = vm.kind.meta.color
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    var anchor by remember { mutableStateOf(Offset.Zero) }

    val total = vm.exercises.size
    val pct = if (total == 0) 0f else (vm.currentIndex + 1f) / total

    fun finalize() = vm.finish { id -> onFinished(id, anchor) }

    Box(
        Modifier
            .fillMaxSize()
            .background(FT.bg)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(acc.copy(alpha = 0.15f), Color.Transparent),
                        center = Offset(size.width / 2f, 0f),
                        radius = size.width * 0.85f,
                    ),
                )
            }
            .padding(top = statusTop),
    ) {
        Column(Modifier.fillMaxSize()) {
            // Progress bar
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(FT.whiteA06),
            ) {
                val w by animateFloatAsState(pct, tween(300), label = "wprog")
                Box(
                    Modifier
                        .fillMaxWidth(w)
                        .height(2.dp)
                        .background(acc),
                )
            }

            // Close
            Row(
                Modifier.fillMaxWidth().padding(start = 12.dp, end = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier.size(40.dp).ftClick { finalize() },
                    contentAlignment = Alignment.Center,
                ) { FtIcon("close", size = 20.dp, color = FT.text2) }
            }

            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
            ) {
                AnimatedVisibility(
                    visible = vm.pbBanner != null,
                    enter = fadeIn(tween(300)) + expandVertically(tween(300)),
                    exit = fadeOut(tween(200)) + shrinkVertically(tween(200)),
                ) {
                    vm.pbBanner?.let { PbBanner(it.value, it.delta) }
                }

                FtText(
                    if (vm.isCardio) vm.kind.meta.label.uppercase()
                    else "EXERCISE ${vm.currentIndex + 1} OF $total",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    color = FT.text3,
                    size = 11,
                    letterSpacingEm = 0.12f,
                    align = TextAlign.Center,
                )
                FtText(
                    fmt(vm.elapsed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    color = FT.text,
                    size = 88,
                    family = FontHeadline,
                    letterSpacingEm = -0.08f,
                    lineHeightEm = 1f,
                    align = TextAlign.Center,
                )
                FtText(
                    vm.exercises.getOrElse(vm.currentIndex) { "" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    color = FT.text,
                    size = 28,
                    family = FontFine,
                    italic = true,
                    letterSpacingEm = -0.05f,
                    align = TextAlign.Center,
                )

                Spacer(Modifier.height(32.dp))

                when {
                    vm.isCardio && vm.cardioDone -> CardioDistance(vm, acc)
                    vm.isCardio -> CardioRunning(acc) { vm.stopCardioTimer() }
                    else -> RoutineBody(vm, acc)
                }

                Spacer(Modifier.height(24.dp))
            }

            Box(
                Modifier
                    .padding(start = 24.dp, end = 24.dp, bottom = navBottom + 20.dp)
                    .fillMaxWidth()
                    .rippleAnchor { anchor = it },
            ) {
                val last = vm.isCardio || vm.isLastExercise
                FtPrimaryButton(
                    if (last) "Finish" else "Next exercise",
                    color = acc,
                ) {
                    if (last) finalize() else vm.next()
                }
            }
        }
    }
}

@Composable
private fun PbBanner(record: String, delta: String?) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(FT.orangeA06)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            Modifier
                .size(4.dp, 32.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(FT.orange),
        )
        Column(Modifier.weight(1f)) {
            FtText("NEW RECORD", color = FT.orange, size = 11, letterSpacingEm = 0.12f)
            FtText(
                record,
                modifier = Modifier.padding(top = 4.dp),
                color = FT.text,
                size = 15,
                family = FontFine,
                italic = true,
            )
        }
        if (delta != null) FtText(delta, color = FT.text3, size = 11)
    }
}

@Composable
private fun CardioRunning(acc: Color, onStop: () -> Unit) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(acc))
            FtText("TRACKING", color = FT.text3, size = 11, letterSpacingEm = 0.12f)
        }
        Row(
            Modifier
                .pressScale(0.97f)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, FT.whiteA08, RoundedCornerShape(16.dp))
                .ftClick(onStop)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(Modifier.size(10.dp).background(FT.text2))
            FtText("Stop timer", color = FT.text, size = 14)
        }
    }
}

@Composable
private fun CardioDistance(vm: WorkoutViewModel, acc: Color) {
    val cfg = vm.cfg ?: return
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        FtText(
            "TRACKED TIME · ${fmt(vm.elapsed)}",
            color = FT.text3,
            size = 10,
            letterSpacingEm = 0.12f,
        )
        FtText(
            "HOW FAR?",
            modifier = Modifier.padding(top = 28.dp, bottom = 10.dp),
            color = FT.text3,
            size = 10,
            letterSpacingEm = 0.12f,
        )
        Row(
            Modifier.width(220.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(Modifier.width(130.dp)) {
                FtUnderlineInput(
                    value = vm.distance,
                    onValueChange = { vm.updateDistance(it) },
                    placeholder = if (cfg.decimal) "0.0" else "0",
                    size = 32,
                    family = FontHeadline,
                    underlineColor = acc,
                    align = TextAlign.Center,
                    keyboardType = KeyboardType.Decimal,
                )
            }
            Spacer(Modifier.width(8.dp))
            FtText(cfg.unitLabel, color = FT.text3, size = 14, modifier = Modifier.padding(bottom = 8.dp))
        }
        Row(
            Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cfg.presets.forEach { v ->
                val on = vm.distance == v
                Box(
                    Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (on) acc else FT.raised)
                        .ftClick { vm.updateDistance(v) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    FtText(v, color = if (on) FT.text else FT.text2, size = 12)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            CalcTile("Pace", cfg.paceUnit, vm.paceString(), Modifier.weight(1f))
            CalcTile(
                "Calories",
                "kcal",
                if ((vm.distance.toFloatOrNull() ?: 0f) > 0f) "${vm.calories()}" else "—",
                Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CalcTile(label: String, unit: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        FtText(label.uppercase(), color = FT.text3, size = 10, letterSpacingEm = 0.12f)
        Row(
            Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FtText(value, color = FT.text, size = 24, family = FontFine, italic = true)
            FtText(unit, color = FT.text3, size = 11)
        }
    }
}

@Composable
private fun RoutineBody(vm: WorkoutViewModel, acc: Color) {
    val sets = vm.setsFor(vm.currentIndex)
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FtText("SET", modifier = Modifier.width(40.dp), color = FT.text3, size = 10, letterSpacingEm = 0.12f)
            FtText("REPS", modifier = Modifier.weight(1f), color = FT.text3, size = 10, letterSpacingEm = 0.12f, align = TextAlign.Center)
            FtText("KG", modifier = Modifier.weight(1f), color = FT.text3, size = 10, letterSpacingEm = 0.12f, align = TextAlign.Center)
            Box(Modifier.size(36.dp))
        }
        sets.forEach { row -> SetRowView(row, acc) { vm.checkSet(row) } }

        Box(
            Modifier
                .fillMaxWidth()
                .pressScale(0.99f)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, FT.whiteA08, RoundedCornerShape(14.dp))
                .ftClick { vm.addSet() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FtIcon("plus", size = 14.dp, color = FT.text2)
                FtText("Add set", color = FT.text2, size = 13)
            }
        }

        AnimatedVisibility(visible = vm.restActive) {
            RestTimer(vm.restRemaining, vm.restTotal, acc, { vm.skipRest() }, { vm.addRest30() })
        }
    }
}

@Composable
private fun SetRowView(row: SetRow, acc: Color, onCheck: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FtText(
            "${row.number}",
            modifier = Modifier.width(40.dp),
            color = FT.text2,
            size = 15,
            family = FontHeadline,
        )
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(FT.raised)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            FtUnderlineInput(
                value = row.reps,
                onValueChange = { row.reps = it.filter(Char::isDigit) },
                placeholder = "0",
                size = 16,
                family = FontHeadline,
                underlineColor = acc,
                align = TextAlign.Center,
                keyboardType = KeyboardType.Number,
            )
        }
        Box(
            Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(FT.raised)
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            FtUnderlineInput(
                value = row.weight,
                onValueChange = { row.weight = it.filter { c -> c.isDigit() || c == '.' } },
                placeholder = "0",
                size = 16,
                family = FontHeadline,
                underlineColor = acc,
                align = TextAlign.Center,
                keyboardType = KeyboardType.Decimal,
            )
        }
        Box(
            Modifier
                .size(36.dp)
                .pressScale(0.94f)
                .clip(RoundedCornerShape(12.dp))
                .background(if (row.checked) acc else FT.raised)
                .ftClick(onCheck),
            contentAlignment = Alignment.Center,
        ) {
            FtIcon("check", size = 16.dp, color = if (row.checked) FT.text else FT.text3, strokeWidth = 2.4f)
        }
    }
}

@Composable
private fun RestTimer(
    remaining: Int,
    total: Int,
    acc: Color,
    onSkip: () -> Unit,
    onAdd30: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        FtText("REST", color = FT.text3, size = 10, letterSpacingEm = 0.12f)
        Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
            val p = if (total == 0) 0f else (total - remaining).toFloat() / total
            Canvas(Modifier.size(120.dp)) {
                val sw = 4.dp.toPx()
                drawArc(
                    color = FT.whiteA06,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(sw),
                    size = Size(size.width - sw, size.height - sw),
                    topLeft = Offset(sw / 2, sw / 2),
                )
                drawArc(
                    color = acc,
                    startAngle = -90f,
                    sweepAngle = 360f * p,
                    useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round),
                    size = Size(size.width - sw, size.height - sw),
                    topLeft = Offset(sw / 2, sw / 2),
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FtText(
                    "0:%02d".format(remaining),
                    color = FT.text,
                    size = 28,
                    family = FontHeadline,
                    letterSpacingEm = -0.05f,
                    lineHeightEm = 1f,
                )
                FtText(
                    "OF 1:00",
                    modifier = Modifier.padding(top = 6.dp),
                    color = FT.text3,
                    size = 10,
                    letterSpacingEm = 0.12f,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                Modifier
                    .pressScale(0.97f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, FT.whiteA08, RoundedCornerShape(14.dp))
                    .ftClick(onAdd30)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            ) { FtText("+ 30s", color = FT.text2, size = 13) }
            Box(
                Modifier
                    .pressScale(0.97f)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, FT.whiteA08, RoundedCornerShape(14.dp))
                    .ftClick(onSkip)
                    .padding(horizontal = 18.dp, vertical = 10.dp),
            ) { FtText("Skip", color = FT.text2, size = 13) }
        }
    }
}
