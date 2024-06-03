
package io.github.alexzhirkevich.compottie.internal.animation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("a")
internal sealed interface AnimatedColor : KeyframeAnimation<Color>, Indexable {

    @Serializable
    @SerialName("0")
    class Default(
        @SerialName("k")
        val value: FloatArray,

        @SerialName("x")
        override val expression: String? = null,

        @SerialName("ix")
        override val index: String? = null
    ) : AnimatedColor {

        @Transient
        private val color: Color = value.toColor()

        override fun interpolated(frame: Float) = color
    }

    @Serializable
    @SerialName("1")
    class Animated(

        @SerialName("k")
        val value: List<VectorKeyframe>,

        @SerialName("x")
        override val expression: String? = null,

        @SerialName("ix")
        override val index: String? = null
    ) : AnimatedColor, KeyframeAnimation<Color> by BaseKeyframeAnimation(
        keyframes = value,
        emptyValue = Color.Transparent,
        map = { s, e, p, _ ->
            lerp(s.toColor(), e.toColor(), easingX.transform(p))
        }
    )
}

internal fun FloatArray.toColor() = Color(
    red = get(0),
    green = get(1),
    blue = get(2),
    alpha = getOrNull(3) ?: 1f
)



