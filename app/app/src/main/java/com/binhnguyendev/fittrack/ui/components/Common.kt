package com.binhnguyendev.fittrack.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.binhnguyendev.fittrack.data.db.ActivityKind
import com.binhnguyendev.fittrack.ui.theme.FT
import com.binhnguyendev.fittrack.ui.theme.FontFine
import com.binhnguyendev.fittrack.ui.theme.FontHeadline
import com.binhnguyendev.fittrack.ui.theme.FontText
import com.binhnguyendev.fittrack.ui.theme.meta
import java.util.Calendar

// ── Interaction helpers ────────────────────────────────────────────────────

/** Press-scale gesture (prototype usePress). */
fun Modifier.pressScale(pressedScale: Float = 0.97f, durationMs: Int = 120): Modifier =
    composed {
        var pressed by remember { mutableStateOf(false) }
        val s by animateFloatAsState(
            targetValue = if (pressed) pressedScale else 1f,
            animationSpec = tween(durationMs),
            label = "pressScale",
        )
        this
            .scale(s)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    pressed = true
                    waitForUpOrCancellation()
                    pressed = false
                }
            }
    }

/** Click with no Material ripple/indication (the UI uses press-scale instead). */
fun Modifier.ftClick(onClick: () -> Unit): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick,
    )
}

// ── FtText: funnels all text so a Tiempos family is always explicit ────────
@Composable
fun FtText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = FT.text,
    size: Int = 14,
    weight: FontWeight = FontWeight.Normal,
    family: FontFamily = FontText,
    italic: Boolean = false,
    letterSpacingEm: Float = 0.01f,
    lineHeightEm: Float = 1.3f,
    align: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = size.sp,
        fontFamily = family,
        fontWeight = weight,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        letterSpacing = (size * letterSpacingEm).sp,
        lineHeight = (size * lineHeightEm).sp,
        textAlign = align,
        maxLines = maxLines,
    )
}

// ── FtScreen — scrollable content area, 24dp horizontal padding ────────────
@Composable
fun FtScreen(
    modifier: Modifier = Modifier,
    scroll: Boolean = true,
    tabBar: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(
        Modifier
            .fillMaxSize()
            .background(FT.bg),
    ) {
        val scrollMod = if (scroll) Modifier.verticalScroll(rememberScrollState()) else Modifier
        Column(
            modifier
                .fillMaxSize()
                .then(scrollMod)
                .padding(
                    PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = statusTop + 14.dp,
                        bottom = if (tabBar) 104.dp else 24.dp,
                    ),
                ),
            content = content,
        )
    }
}

// ── FtCard ─────────────────────────────────────────────────────────────────
@Composable
fun FtCard(
    modifier: Modifier = Modifier,
    padded: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.pressScale(0.98f) else Modifier)
            .clip(RoundedCornerShape(24.dp))
            .background(FT.surface)
            .then(if (onClick != null) Modifier.ftClick(onClick) else Modifier)
            .padding(if (padded) 20.dp else 0.dp),
        content = content,
    )
}

// ── FtSectionLabel ─────────────────────────────────────────────────────────
@Composable
fun FtSectionLabel(text: String, modifier: Modifier = Modifier, topPadding: Dp = 28.dp) {
    FtText(
        text = text.uppercase(),
        modifier = modifier.padding(top = topPadding, bottom = 10.dp),
        color = FT.text3,
        size = 10,
        weight = FontWeight.Medium,
        letterSpacingEm = 0.12f,
        lineHeightEm = 1.2f,
    )
}

// ── Buttons ────────────────────────────────────────────────────────────────
@Composable
fun FtPrimaryButton(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = FT.orange,
    fullWidth: Boolean = true,
    large: Boolean = true,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val padY = if (large) 16.dp else 12.dp
    val padX = if (large) 22.dp else 16.dp
    val fs = if (large) 15 else 14
    Box(
        modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .then(if (enabled) Modifier.pressScale(0.97f) else Modifier)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) color else FT.raised)
            .then(if (enabled) Modifier.ftClick(onClick) else Modifier)
            .padding(horizontal = padX, vertical = padY),
        contentAlignment = Alignment.Center,
    ) {
        FtText(
            text = label,
            color = if (enabled) FT.text else FT.text3,
            size = fs,
            weight = FontWeight.Medium,
            lineHeightEm = 1f,
        )
    }
}

@Composable
fun FtGhostButton(
    label: String,
    modifier: Modifier = Modifier,
    fullWidth: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .pressScale(0.97f)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, FT.whiteA08, RoundedCornerShape(16.dp))
            .ftClick(onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        FtText(label, color = FT.text, size = 14, weight = FontWeight.Medium, lineHeightEm = 1f)
    }
}

// ── Activity bits ──────────────────────────────────────────────────────────
@Composable
fun ActivityChip(kind: ActivityKind, size: Dp = 36.dp) {
    val m = kind.meta
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(m.soft),
        contentAlignment = Alignment.Center,
    ) {
        FtIcon(m.icon, size = size * 0.5f, color = m.color, strokeWidth = 1.8f)
    }
}

@Composable
fun ActivityDot(kind: ActivityKind, size: Dp = 8.dp) {
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(kind.meta.color),
    )
}

@Composable
fun ActivityStrip(kind: ActivityKind, height: Dp = 40.dp) {
    Box(
        Modifier
            .size(4.dp, height)
            .clip(RoundedCornerShape(999.dp))
            .background(kind.meta.color),
    )
}

@Composable
fun ActivityTag(kind: ActivityKind, label: String? = null) {
    FtText(
        text = (label ?: kind.meta.label).uppercase(),
        color = kind.meta.color,
        size = 10,
        weight = FontWeight.Medium,
        letterSpacingEm = 0.12f,
        lineHeightEm = 1f,
    )
}

// ── Skeleton (shimmer sweep) ───────────────────────────────────────────────
@Composable
private fun BoxScope.ShimmerOverlay() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val x by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "shimmerX",
    )
    Box(
        Modifier
            .matchParentSize()
            .drawWithContent {
                drawContent()
                val w = this.size.width
                drawRect(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, FT.whiteA04, Color.Transparent),
                        startX = x * w,
                        endX = x * w + w,
                    ),
                )
            },
    )
}

@Composable
fun Skeleton(width: Dp, height: Dp, radius: Dp = 8.dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(width, height)
            .clip(RoundedCornerShape(radius))
            .background(FT.raised),
    ) { ShimmerOverlay() }
}

@Composable
fun SkeletonFill(height: Dp, radius: Dp = 8.dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(radius))
            .background(FT.raised),
    ) { ShimmerOverlay() }
}

// ── Underline input affordance ─────────────────────────────────────────────
@Composable
fun FtUnderline(focused: Boolean, color: Color = FT.orange, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(FT.whiteA08),
    ) {
        val w by animateFloatAsState(
            targetValue = if (focused) 1f else 0f,
            animationSpec = tween(200),
            label = "underline",
        )
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(w)
                .background(color),
        )
    }
}

// ── StreakBadge ────────────────────────────────────────────────────────────
@Composable
fun StreakBadge(count: Int) {
    Row(
        Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(FT.orangeA10)
            .border(1.dp, FT.orangeA30, RoundedCornerShape(999.dp))
            .padding(start = 10.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FtIcon("streakFlame", size = 14.dp, color = FT.orange)
        Row(verticalAlignment = Alignment.Bottom) {
            FtText("$count", color = FT.orange, size = 14, family = FontFine, italic = true, lineHeightEm = 1f)
            FtText(" consecutive days", color = FT.orange, size = 13, weight = FontWeight.Medium, lineHeightEm = 1f)
        }
    }
}

// ── Avatar ─────────────────────────────────────────────────────────────────
@Composable
fun Avatar(
    name: String,
    photoUri: String?,
    size: Dp = 40.dp,
    onClick: (() -> Unit)? = null,
) {
    val initial = (name.trim().firstOrNull() ?: 'A').uppercaseChar().toString()
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (photoUri != null) FT.black else FT.raised)
            .then(if (onClick != null) Modifier.ftClick(onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = avatarModel(photoUri),
                contentDescription = name,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        } else {
            FtText(
                initial,
                color = FT.text,
                size = (size.value * 0.40f).toInt().coerceAtLeast(10),
                weight = FontWeight.Medium,
                family = FontHeadline,
                lineHeightEm = 1f,
            )
        }
    }
}

/** Stored avatar paths are app-internal files; bare content/URL strings pass
 *  through. Coil needs a File for plain filesystem paths. */
fun avatarModel(photoUri: String): Any =
    if (photoUri.startsWith("/")) java.io.File(photoUri) else photoUri

// ── Time-of-day helpers ────────────────────────────────────────────────────
fun greeting(): String {
    val h = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        h < 5 -> "Good evening,"
        h < 12 -> "Good morning,"
        h < 17 -> "Good afternoon,"
        else -> "Good evening,"
    }
}

fun nowClock(use24h: Boolean): String {
    val c = Calendar.getInstance()
    val m = c.get(Calendar.MINUTE).toString().padStart(2, '0')
    return if (use24h) {
        "${c.get(Calendar.HOUR_OF_DAY).toString().padStart(2, '0')}:$m"
    } else {
        val h = c.get(Calendar.HOUR_OF_DAY)
        val h12 = ((h + 11) % 12) + 1
        "$h12:$m"
    }
}
