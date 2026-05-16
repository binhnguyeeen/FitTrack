package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.ui.components.ActivityChip
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.FtUnderlineInput
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.vm.AddExerciseViewModel
import com.binhnguyendev.fittrack.ui.vm.CreateTemplateViewModel
import com.binhnguyendev.fittrack.ui.vm.PresetExercise
import com.binhnguyendev.fittrack.ui.vm.ftViewModel

@Composable
fun AddExerciseScreen(
    createVm: CreateTemplateViewModel,
    onBack: () -> Unit,
) {
    val vm = ftViewModel { _ -> AddExerciseViewModel() }
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val filtered = vm.filtered

    Column(
        Modifier
            .fillMaxSize()
            .background(FT.bg)
            .padding(top = statusTop),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).ftClick(onBack),
                contentAlignment = Alignment.Center,
            ) { FtIcon("close", size = 20.dp, color = FT.text) }
            FtText(
                "ADD EXERCISE",
                modifier = Modifier.weight(1f),
                color = FT.text2,
                size = 14,
                letterSpacingEm = 0.04f,
                align = TextAlign.Center,
            )
            Box(Modifier.size(40.dp))
        }

        Column(Modifier.padding(horizontal = 24.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                FtIcon("search", size = 16.dp, color = FT.text3)
                Box(Modifier.weight(1f)) {
                    FtUnderlineInput(
                        value = vm.query,
                        onValueChange = { vm.query = it },
                        placeholder = "Search exercises",
                        size = 15,
                        letterSpacingEm = 0.01f,
                    )
                }
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = navBottom + 24.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FtText(
                    "PRESET LIBRARY",
                    modifier = Modifier.weight(1f),
                    color = FT.text3,
                    size = 10,
                    letterSpacingEm = 0.12f,
                )
                FtText("${filtered.size} of ${vm.total}", color = FT.text3, size = 11)
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filtered.forEach { p ->
                    PresetRow(p) {
                        createVm.addExercise(p.name, p.mode)
                        onBack()
                    }
                }
                if (filtered.isEmpty()) {
                    FtText(
                        "No matching exercises",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        color = FT.text3,
                        size = 13,
                        align = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun PresetRow(p: PresetExercise, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .pressScale(0.98f)
            .clip(RoundedCornerShape(24.dp))
            .background(FT.surface)
            .ftClick(onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ActivityChip(p.kind, size = 36.dp)
        Column(Modifier.weight(1f)) {
            FtText(p.name, color = FT.text, size = 14)
            FtText(
                p.mode,
                modifier = Modifier.padding(top = 3.dp),
                color = FT.text2,
                size = 12,
            )
        }
        FtIcon("plus", size = 16.dp, color = FT.text3)
    }
}
