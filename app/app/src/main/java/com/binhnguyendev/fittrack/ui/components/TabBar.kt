package com.binhnguyendev.fittrack.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.binhnguyendev.fittrack.navigation.Routes
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FT_EASE

private data class Tab(val route: String, val icon: String, val label: String)

private val TABS = listOf(
    Tab(Routes.HOME, "home", "Home"),
    Tab(Routes.CALENDAR, "calendar", "Calendar"),
    Tab(Routes.TEMPLATES, "clip", "Templates"),
    Tab(Routes.STATS, "chart", "Stats"),
)

/**
 * Floating pill tab bar (prototype TabBar) — translucent #1A1A1C, hairline
 * border, sliding orange active-pill indicator. The blur the prototype uses is
 * approximated by the 0.92-opacity fill (true backdrop blur isn't cheap in
 * Compose).
 */
@Composable
fun TabBar(activeRoute: String?, onSelect: (String) -> Unit) {
    val activeIdx = TABS.indexOfFirst { it.route == activeRoute }.coerceAtLeast(0)
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(1000.dp))
            .background(FT.tabBarBg)
            .border(1.dp, FT.whiteA05, RoundedCornerShape(1000.dp))
            .padding(6.dp),
    ) {
        val slot = (maxWidth - 0.dp) / TABS.size
        val indicatorX by animateDpAsState(
            targetValue = slot * activeIdx,
            animationSpec = tween(250, easing = FT_EASE),
            label = "tabIndicator",
        )
        Box(
            Modifier
                .offset(x = indicatorX)
                .size(slot, 52.dp)
                .clip(RoundedCornerShape(1000.dp))
                .background(FT.orangeA12),
        )
        Row(Modifier.fillMaxWidth().fillMaxHeight()) {
            TABS.forEachIndexed { i, tab ->
                val on = i == activeIdx
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .ftClick { onSelect(tab.route) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    FtIcon(
                        tab.icon,
                        size = 20.dp,
                        color = if (on) FT.orange else FT.text3,
                        strokeWidth = if (on) 2f else 1.8f,
                    )
                    FtText(
                        tab.label,
                        color = if (on) FT.orange else FT.text3,
                        size = 10,
                        weight = FontWeight.Medium,
                        letterSpacingEm = 0.02f,
                        lineHeightEm = 1.6f,
                    )
                }
            }
        }
    }
}
