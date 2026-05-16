package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.binhnguyendev.fittrack.ui.components.ActivityDot
import com.binhnguyendev.fittrack.ui.components.FtPrimaryButton
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.nav.rippleAnchor
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.FontText
import com.binhnguyendev.fittrack.ui.vm.StatCell
import com.binhnguyendev.fittrack.ui.vm.SummaryViewModel

@Composable
fun SummaryScreen(
    vm: SummaryViewModel,
    onDone: (Offset) -> Unit,
) {
    val state = vm.state
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    var anchor by remember { mutableStateOf(Offset.Zero) }

    Box(
        Modifier
            .fillMaxSize()
            .background(FT.bg)
            .drawBehind {
                drawRect(
                    Brush.radialGradient(
                        colors = listOf(FT.orange.copy(alpha = 0.09f), Color.Transparent),
                        center = Offset(size.width / 2f, 0f),
                        radius = size.width * 0.85f,
                    ),
                )
            }
            .padding(top = statusTop),
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, end = 24.dp, top = 40.dp),
            ) {
                FtText(
                    "Workout complete.",
                    modifier = Modifier.fillMaxWidth(),
                    color = FT.text,
                    size = 36,
                    family = FontFine,
                    italic = true,
                    letterSpacingEm = -0.05f,
                    lineHeightEm = 1.05f,
                    align = TextAlign.Center,
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ActivityDot(state.kind)
                    Spacer(Modifier.size(10.dp))
                    FtText(state.title, color = FT.text2, size = 13)
                    FtText("  ·  ${state.dateLabel}", color = FT.text3, size = 13)
                }

                // 2×2 stats grid
                val cells = state.stats
                Column(
                    Modifier.padding(top = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    cells.chunked(2).forEach { rowCells ->
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(32.dp),
                        ) {
                            rowCells.forEach { c ->
                                StatView(c, Modifier.weight(1f))
                            }
                            if (rowCells.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }

                if (state.pb != null) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(FT.orangeA06)
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        Box(
                            Modifier
                                .size(4.dp, 36.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(FT.orange),
                        )
                        Column(Modifier.weight(1f)) {
                            FtText("NEW RECORD", color = FT.orange, size = 11, letterSpacingEm = 0.12f)
                            FtText(
                                state.pb,
                                modifier = Modifier.padding(top = 4.dp),
                                color = FT.text,
                                size = 15,
                                family = FontFine,
                                italic = true,
                            )
                        }
                    }
                }

                FtSectionLabel("Notes")
                BasicTextField(
                    value = state.notes,
                    onValueChange = vm::onNotesChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 60.dp)
                        .drawBehind {
                            val y = size.height - 1.dp.toPx()
                            drawLine(
                                FT.whiteA08,
                                Offset(0f, y),
                                Offset(size.width, y),
                                strokeWidth = 1.dp.toPx(),
                            )
                        }
                        .padding(vertical = 6.dp),
                    textStyle = TextStyle(
                        color = FT.text,
                        fontFamily = FontText,
                        fontSize = 14.sp,
                        lineHeight = 21.sp,
                    ),
                    cursorBrush = SolidColor(FT.orange),
                )

                Spacer(Modifier.height(24.dp))
            }

            Box(
                Modifier
                    .padding(start = 24.dp, end = 24.dp, bottom = navBottom + 20.dp)
                    .fillMaxWidth()
                    .rippleAnchor { anchor = it },
            ) {
                FtPrimaryButton("Done") {
                    vm.persistNotes()
                    onDone(anchor)
                }
            }
        }
    }
}

@Composable
private fun StatView(c: StatCell, modifier: Modifier = Modifier) {
    Column(modifier) {
        FtText(
            c.value,
            color = FT.text,
            size = 48,
            family = FontHeadline,
            letterSpacingEm = -0.05f,
            lineHeightEm = 1f,
        )
        Row(Modifier.padding(top = 8.dp)) {
            FtText(
                c.label.uppercase(),
                color = FT.text3,
                size = 11,
                letterSpacingEm = 0.12f,
            )
            FtText(
                "  ${c.unit}",
                color = FT.text3,
                size = 11,
                letterSpacingEm = 0.12f,
            )
        }
    }
}
