package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.ui.components.ActivityChip
import com.binhnguyendev.fittrack.ui.components.FtIcon
import com.binhnguyendev.fittrack.ui.components.FtScreen
import com.binhnguyendev.fittrack.ui.components.FtSectionLabel
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.components.Skeleton
import com.binhnguyendev.fittrack.ui.components.pressScale
import com.binhnguyendev.fittrack.ui.components.ftClick
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.vm.TemplatesViewModel
import com.binhnguyendev.fittrack.ui.vm.ftViewModel

@Composable
fun TemplatesScreen(onCreate: () -> Unit) {
    val vm = ftViewModel { repos -> TemplatesViewModel(repos) }
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        FtScreen {
            FtText(
                "Templates",
                modifier = Modifier.padding(top = 8.dp),
                color = FT.text,
                size = 28,
                family = FontHeadline,
                letterSpacingEm = -0.05f,
                lineHeightEm = 1.1f,
            )
            FtSectionLabel("Saved")
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (state.loading) {
                    repeat(4) { TemplateSkeleton() }
                } else if (state.templates.isEmpty()) {
                    FtText(
                        "No templates yet — tap + to create one.",
                        modifier = Modifier.padding(vertical = 14.dp),
                        color = FT.text3,
                        size = 13,
                    )
                } else {
                    state.templates.forEach {
                        TemplateCard(it.template.name, it.template.activityKind, it.exerciseCount)
                    }
                }
            }
        }

        // FAB
        Box(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 104.dp)
                .size(56.dp)
                .pressScale(0.95f)
                .clip(CircleShape)
                .background(FT.orange)
                .ftClick(onCreate),
            contentAlignment = Alignment.Center,
        ) {
            FtIcon("plus", size = 22.dp, color = FT.text, strokeWidth = 2.2f)
        }
    }
}

@Composable
private fun TemplateCard(name: String, kind: ActivityKind, count: Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp)
            .pressScale(0.98f)
            .clip(RoundedCornerShape(24.dp))
            .background(FT.surface)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ActivityChip(kind, size = 40.dp)
        Column(Modifier.weight(1f)) {
            FtText(name, color = FT.text, size = 16, lineHeightEm = 1.2f)
            FtText(
                "$count exercises",
                modifier = Modifier.padding(top = 4.dp),
                color = FT.text2,
                size = 12,
            )
        }
        FtIcon("arrow", size = 16.dp, color = FT.text3)
    }
}

@Composable
private fun TemplateSkeleton() {
    Row(
        Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(FT.surface)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Skeleton(40.dp, 40.dp, 14.dp)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Skeleton(140.dp, 14.dp, 7.dp)
            Skeleton(80.dp, 10.dp, 5.dp)
        }
    }
}
