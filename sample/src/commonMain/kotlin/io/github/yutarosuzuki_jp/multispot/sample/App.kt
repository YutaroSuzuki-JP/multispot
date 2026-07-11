package io.github.yutarosuzuki_jp.multispot.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect as ComposeRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.yutarosuzuki_jp.multispot.BalloonArrowPosition
import io.github.yutarosuzuki_jp.multispot.BalloonShape
import io.github.yutarosuzuki_jp.multispot.MultispotArea
import io.github.yutarosuzuki_jp.multispot.SpotShape
import io.github.yutarosuzuki_jp.multispot.TooltipDirection
import io.github.yutarosuzuki_jp.multispot.TooltipStyle
import io.github.yutarosuzuki_jp.multispot.multispot
import io.github.yutarosuzuki_jp.multispot.rememberMultispotState

class DiamondShape(override val margin: Dp = 8.dp) : SpotShape {
    override fun drawCutout(drawScope: DrawScope, rect: ComposeRect) {
        val expanded = drawScope.getExpandedRect(rect)
        val path = Path().apply {
            moveTo(expanded.center.x, expanded.top)
            lineTo(expanded.right, expanded.center.y)
            lineTo(expanded.center.x, expanded.bottom)
            lineTo(expanded.left, expanded.center.y)
            close()
        }
        drawScope.drawPath(
            path = path,
            color = Color.Transparent,
            blendMode = BlendMode.Clear
        )
    }
}

@Composable
fun App() {
    val state = rememberMultispotState()
    var demoMode by remember { mutableStateOf("multi") }
    var statusText by remember { mutableStateOf("Ready") }
    var selectedStyle by remember { mutableStateOf(TooltipStyle.Balloon) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF8B5CF6),
            background = Color(0xFF0F172A),
            surface = Color(0xFF1E293B)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            MultispotArea(
                state = state,
                overlayColor = Color.Black.copy(alpha = 0.8f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E1B4B)
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✨ Multispot Custom Demo ✨",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Status: $statusText",
                            fontSize = 14.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    if (demoMode == "multi") {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 100.dp)
                                .size(56.dp)
                                .multispot(
                                    state = state,
                                    step = 0,
                                    key = "top_target",
                                    shape = SpotShape.Circle(radius = 32.dp),
                                    tooltipStyle = selectedStyle,
                                    preferredDirection = TooltipDirection.Top,
                                    tooltip = { arrowPos ->
                                        TooltipBalloon(
                                            title = "Top Direction",
                                            message = "This tooltip is configured to show on TOP.",
                                            style = selectedStyle,
                                            arrowPosition = arrowPos
                                        )
                                    }
                                )
                                .background(Color(0xFFEC4899), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 16.dp)
                                .size(56.dp)
                                .multispot(
                                    state = state,
                                    step = 1,
                                    key = "left_target",
                                    shape = SpotShape.Circle(radius = 32.dp),
                                    tooltipStyle = selectedStyle,
                                    preferredDirection = TooltipDirection.Left,
                                    tooltip = { arrowPos ->
                                        TooltipBalloon(
                                            title = "Left Direction",
                                            message = "This tooltip is configured to show on the LEFT.",
                                            style = selectedStyle,
                                            arrowPosition = arrowPos
                                        )
                                    }
                                )
                                .background(Color(0xFF3B82F6), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .size(56.dp)
                                .multispot(
                                    state = state,
                                    step = 2,
                                    key = "right_target",
                                    shape = SpotShape.Circle(radius = 32.dp),
                                    tooltipStyle = selectedStyle,
                                    preferredDirection = TooltipDirection.Right,
                                    tooltip = { arrowPos ->
                                        TooltipBalloon(
                                            title = "Right Direction",
                                            message = "This tooltip is configured to show on the RIGHT.",
                                            style = selectedStyle,
                                            arrowPosition = arrowPos
                                        )
                                    }
                                )
                                .background(Color(0xFF10B981), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color.White)
                        }

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 120.dp)
                                .size(56.dp)
                                .multispot(
                                    state = state,
                                    step = 3,
                                    key = "bottom_target",
                                    shape = SpotShape.Circle(radius = 32.dp),
                                    tooltipStyle = selectedStyle,
                                    preferredDirection = TooltipDirection.Bottom,
                                    tooltip = { arrowPos ->
                                        TooltipBalloon(
                                            title = "Bottom Direction",
                                            message = "This tooltip is configured to show on the BOTTOM.",
                                            style = selectedStyle,
                                            arrowPosition = arrowPos
                                        )
                                    }
                                )
                                .background(Color(0xFFF59E0B), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .offset(y = (-80).dp)
                                .size(72.dp)
                                .multispot(
                                    state = state,
                                    step = 4,
                                    key = "single_target",
                                    shape = DiamondShape(margin = 8.dp),
                                    tooltipStyle = selectedStyle,
                                    preferredDirection = TooltipDirection.Auto,
                                    tooltip = { arrowPos ->
                                        TooltipBalloon(
                                            title = "Diamond Custom Shape",
                                            message = "Using custom Diamond SpotShape cutout & single-step mode!",
                                            style = selectedStyle,
                                            arrowPosition = arrowPos
                                        )
                                    }
                                )
                                .background(Color(0xFF8B5CF6), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = "Star", tint = Color.White, modifier = Modifier.size(36.dp))
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = 120.dp)
                            .width(320.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    demoMode = "multi"
                                    statusText = "Multi-step loaded"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (demoMode == "multi") Color(0xFF8B5CF6) else Color(0xFF334155)
                                )
                            ) {
                                Text("Multi-Step")
                            }

                            Button(
                                onClick = {
                                    demoMode = "single"
                                    statusText = "Single-step loaded"
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (demoMode == "single") Color(0xFF8B5CF6) else Color(0xFF334155)
                                )
                            ) {
                                Text("Single-Step")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Select Tooltip Style", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf(TooltipStyle.Balloon, TooltipStyle.Arrow, TooltipStyle.Glass, TooltipStyle.Outline, TooltipStyle.Custom).forEach { style ->
                                Button(
                                    onClick = {
                                        selectedStyle = style
                                        statusText = "Style: ${style.name}"
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedStyle == style) Color(0xFFEC4899) else Color(0xFF334155)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp),
                                    modifier = Modifier.weight(1f).padding(horizontal = 0.5.dp)
                                ) {
                                    Text(style.name, fontSize = 8.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                statusText = "Walkthrough active"
                                state.start()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEC4899))
                        ) {
                            Text(
                                text = "Start Walkthrough",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TooltipBalloon(
    title: String,
    message: String,
    style: TooltipStyle,
    arrowPosition: BalloonArrowPosition
) {
    if (style == TooltipStyle.Custom) {
        val customShape = BalloonShape(arrowPosition, arrowSize = 16.dp, cornerRadius = 20.dp)
        Surface(
            shape = customShape,
            color = Color(0xFFF43F5E),
            shadowElevation = 16.dp,
            modifier = Modifier.border(2.dp, Color.White, customShape)
        ) {
            Column(
                modifier = Modifier
                    .width(180.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "🔥 $title",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )
            }
        }
    } else if (style != TooltipStyle.Arrow) {
        Column(modifier = Modifier.width(180.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                fontSize = 12.sp,
                color = if (style == TooltipStyle.Glass) Color.White.copy(alpha = 0.8f) else Color(0xFFCBD5E1),
                lineHeight = 16.sp
            )
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .widthIn(max = 240.dp)
                .padding(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }
    }
}
