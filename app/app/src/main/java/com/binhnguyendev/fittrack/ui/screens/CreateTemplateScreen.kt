package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtUnderlineInput
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.meta
import com.binhnguyendev.fittrack.ui.vm.CreateTemplateViewModel

@Composable
fun CreateTemplateScreen(
    vm: CreateTemplateViewModel,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    onSaved: () -> Unit,
) {
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        Modifier
            .fillMaxSize()
            .background(FT.bg)
            .padding(top = statusTop),
    ) {
        // Top bar
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).ftClick(onBack),
                contentAlignment = Alignment.Center,
            ) { FtIcon("arrowL", size = 20.dp, color = FT.text) }
            FtText(
                "NEW TEMPLATE",
                modifier = Modifier.weight(1f),
                color = FT.text2,
                size = 14,
                letterSpacingEm = 0.04f,
                align = androidx.compose.ui.text.style.TextAlign.Center,
            )
            Box(
                Modifier
                    .pressScale(0.97f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(FT.orange)
                    .ftClick { vm.save(onSaved) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                FtText("Save", color = FT.text, size = 13, weight = androidx.compose.ui.text.font.FontWeight.Medium)
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = navBottom + 32.dp),
        ) {
            FtSectionLabel("Template name", topPadding = 12.dp)
            FtUnderlineInput(
                value = vm.name,
                onValueChange = { vm.name = it },
                placeholder = "Template name",
                size = 24,
                family = FontHeadline,
            )

            FtSectionLabel("Activity")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActivityKind.entries.forEach { k ->
                    val on = k == vm.kind
                    val m = k.meta
                    Column(
                        Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (on) m.soft else Color.Transparent)
                            .border(
                                1.dp,
                                if (on) m.color else FT.whiteA05,
                                RoundedCornerShape(16.dp),
                            )
                            .ftClick { vm.kind = k }
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

            FtSectionLabel("Exercises")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                vm.exercises.forEachIndexed { i, ex ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(FT.surface)
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        FtIcon("drag", size = 18.dp, color = FT.text3, strokeWidth = 2f)
                        Column(Modifier.weight(1f)) {
                            FtText(ex.name, color = FT.text, size = 14)
                            FtText(
                                ex.mode,
                                modifier = Modifier.padding(top = 3.dp),
                                color = FT.text2,
                                size = 12,
                            )
                        }
                        Box(
                            Modifier.size(28.dp).ftClick { vm.removeAt(i) },
                            contentAlignment = Alignment.Center,
                        ) { FtIcon("close", size = 14.dp, color = FT.text3) }
                    }
                }
            }

            Box(
                Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth()
                    .pressScale(0.99f)
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, FT.whiteA10, RoundedCornerShape(24.dp))
                    .ftClick(onAddExercise)
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FtIcon("plus", size = 16.dp, color = FT.text2)
                    FtText("Add exercise", color = FT.text2, size = 14)
                }
            }
        }
    }
}
