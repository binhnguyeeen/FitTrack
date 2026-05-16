package com.binhnguyendev.fittrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.binhnguyendev.fittrack.ui.components.FtText
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontHeadline

/** Temporary placeholder used while the navigation skeleton is wired up
 *  (Milestone 2). Replaced by the real screen in later milestones. */
@Composable
fun PlaceholderScreen(name: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(FT.bg),
        contentAlignment = Alignment.Center,
    ) {
        FtText(name, color = FT.text2, size = 22, family = FontHeadline)
    }
}
