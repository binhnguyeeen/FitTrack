package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.ui.components.ActivityChip
import com.binhnguyendev.fittrack.ui.components.FtCard
import com.binhnguyendev.fittrack.ui.components.FtScreen
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.Skeleton
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.meta
import com.binhnguyendev.fittrack.ui.vm.StatsViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel

@Composable
fun StatsScreen() {
    val vm = ftViewModel { repos -> StatsViewModel(repos) }
    val state by vm.uiState.collectAsStateWithLifecycle()
    var chart by remember { mutableStateOf("line") }

    FtScreen {
        FtText(
            "History",
            modifier = Modifier.padding(top = 8.dp),
            color = FT.text,
            size = 28,
            family = FontHeadline,
            letterSpacingEm = -0.05f,
            lineHeightEm = 1.1f,
        )

        FtSectionLabel("Last 8 weeks")
        FtCard {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                state.heat.forEach { col ->
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        col.forEach { lvl ->
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(FT.heat(lvl)),
                            )
                        }
                    }
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FtText("Less", color = FT.text3, size = 10, letterSpacingEm = 0.04f)
                (0..4).forEach {
                    Box(
                        Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(FT.heat(it)),
                    )
                }
                FtText("More", color = FT.text3, size = 10, letterSpacingEm = 0.04f)
            }
        }

        // Chart toggle
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(FT.raised)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            listOf("line" to "Duration", "bar" to "Volume").forEach { (k, lbl) ->
                val on = chart == k
                Box(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (on) FT.surface else androidx.compose.ui.graphics.Color.Transparent)
                        .ftClick { chart = k }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    FtText(lbl, color = if (on) FT.text else FT.text2, size = 13)
                }
            }
        }
        Box(Modifier.padding(top = 16.dp)) {
            if (chart == "line") LineChart() else BarChart()
        }

        FtSectionLabel("Personal bests")
        Column {
            state.pbs.forEach { p ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ActivityChip(p.kind, size = 24.dp)
                    FtText(p.label, modifier = Modifier.weight(1f), color = FT.text, size = 13)
                    FtText(p.detail, color = FT.text, size = 14, family = FontFine, italic = true)
                    FtText(
                        p.date,
                        modifier = Modifier.padding(start = 10.dp),
                        color = FT.text3,
                        size = 11,
                    )
                }
            }
        }

        FtSectionLabel("Past sessions")
        Column {
            if (state.loading) {
                repeat(4) {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Skeleton(24.dp, 24.dp, 12.dp)
                        Skeleton(140.dp, 12.dp, 6.dp)
                        Box(Modifier.weight(1f))
                        Skeleton(40.dp, 10.dp, 5.dp)
                        Skeleton(42.dp, 12.dp, 6.dp)
                    }
                }
            } else if (state.sessions.isEmpty()) {
                FtText(
                    "No sessions yet.",
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = FT.text3,
                    size = 13,
                )
            } else {
                state.sessions.forEach { s ->
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        ActivityChip(s.kind, size = 24.dp)
                        FtText(s.name, modifier = Modifier.weight(1f), color = FT.text, size = 14)
                        FtText(s.date, color = FT.text3, size = 12)
                        FtText(
                            s.time,
                            modifier = Modifier.padding(start = 10.dp),
                            color = FT.text,
                            size = 14,
                            family = FontFine,
                            italic = true,
                        )
                    }
                }
            }
        }
    }
}

// Static prototype chart data (spec CONSTRAINT 3).
private val LINE_SERIES = listOf(
    ActivityKind.SWIM to listOf(28, 32, 30, 38, 35, 42, 40, 44),
    ActivityKind.TREADMILL to listOf(25, 28, 26, 31, 30, 29, 33, 31),
    ActivityKind.ROUTINE to listOf(22, 24, 28, 25, 32, 30, 36, 38),
)

@Composable
private fun LineChart() {
    Column {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            val leftPad = 24f
            val rightPad = 24f
            val topPad = 24f
            val bottomPad = 32f
            val maxV = 50f
            val n = 8
            fun x(i: Int) = leftPad + i / (n - 1f) * (size.width - leftPad - rightPad)
            fun y(v: Int) = (size.height - bottomPad) - v / maxV * (size.height - topPad - bottomPad)
            listOf(0, 10, 20, 30, 40, 50).forEach { g ->
                val gy = (size.height - bottomPad) - g / maxV * (size.height - topPad - bottomPad)
                drawLine(FT.whiteA04, Offset(leftPad, gy), Offset(size.width - rightPad, gy), 1f)
            }
            LINE_SERIES.forEach { (kind, pts) ->
                val path = Path().apply {
                    pts.forEachIndexed { i, v ->
                        if (i == 0) moveTo(x(i), y(v)) else lineTo(x(i), y(v))
                    }
                }
                drawPath(path, kind.meta.color, style = Stroke(width = 3f, cap = StrokeCap.Round))
            }
        }
        Row(
            Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LINE_SERIES.forEach { (kind, _) ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(12.dp, 2.dp).background(kind.meta.color))
                    FtText(kind.meta.label, color = FT.text2, size = 11)
                }
            }
        }
    }
}

private val BAR_DATA = listOf(
    intArrayOf(25, 20, 0, 30), intArrayOf(30, 30, 0, 25), intArrayOf(25, 40, 20, 0),
    intArrayOf(30, 30, 0, 35), intArrayOf(35, 0, 25, 30), intArrayOf(30, 45, 0, 40),
    intArrayOf(40, 35, 25, 30), intArrayOf(38, 42, 0, 35),
)
// stack order bottom→top: routine, swim, treadmill, basket
private val STACK = listOf(
    ActivityKind.ROUTINE, ActivityKind.SWIM, ActivityKind.TREADMILL, ActivityKind.BASKETBALL,
)

@Composable
private fun BarChart() {
    Column {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            val pad = 24f
            val maxV = 150f
            listOf(0, 50, 100, 150).forEach { g ->
                val gy = (size.height - pad) - g / maxV * (size.height - pad * 2)
                drawLine(FT.whiteA04, Offset(pad, gy), Offset(size.width - pad, gy), 1f)
            }
            val bw = (size.width - pad * 2) / BAR_DATA.size
            BAR_DATA.forEachIndexed { i, week ->
                val cx = pad + i * bw + bw / 2
                var yBottom = size.height - pad
                STACK.forEachIndexed { si, kind ->
                    val v = week[si]
                    if (v > 0) {
                        val h = v / maxV * (size.height - pad * 2)
                        drawRoundRect(
                            color = kind.meta.color,
                            topLeft = Offset(cx - 9f, yBottom - h),
                            size = androidx.compose.ui.geometry.Size(18f, h),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f),
                        )
                        yBottom -= h
                    }
                }
            }
        }
        Row(
            Modifier.padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            STACK.forEach { kind ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(kind.meta.color))
                    FtText(kind.meta.label, color = FT.text2, size = 11)
                }
            }
        }
    }
}
