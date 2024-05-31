package io.github.alexzhirkevich.compottie.internal.platform

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.setFrom
import androidx.compose.ui.graphics.toArgb


private val tempMatrix = android.graphics.Matrix()

internal actual fun MakeLinearGradient(
    from : Offset,
    to : Offset,
    colors : List<Color>,
    colorStops: List<Float>,
    tileMode: TileMode,
    matrix: Matrix
) = LinearGradientShader(
    from = from,
    to = to,
    colorStops = colorStops,
    tileMode = tileMode,
    colors = colors
).apply {
    tempMatrix.setFrom(matrix)
    setLocalMatrix(tempMatrix)
}

internal actual fun MakeRadialGradient(
    center : Offset,
    radius : Float,
    colors : List<Color>,
    colorStops: List<Float>,
    tileMode: TileMode,
    matrix: Matrix
)  = RadialGradientShader(
    center = center,
    radius = radius,
    colorStops = colorStops,
    tileMode = tileMode,
    colors = colors
).apply {
    tempMatrix.setFrom(matrix)
    setLocalMatrix(tempMatrix)
}

