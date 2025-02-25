
package io.github.alexzhirkevich.compottie.internal.animation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import io.github.alexzhirkevich.compottie.internal.AnimationState
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = AnimatedColorSerializer::class)
internal sealed class AnimatedColor : ExpressionProperty<Color>() {

    abstract fun copy() : AnimatedColor

    override fun mapEvaluated(e: Any): Color {
        return when (e){
            is Color -> e
            is List<*> -> (e as List<Number>).toColor2()

            else -> error("Can't convert $e to color")
        }
    }

    @Serializable
    class Default(
        @SerialName("k")
        val value: List<Float>,

        @SerialName("x")
        override val expression: String? = null,

        @SerialName("ix")
        override val index: Int? = null
    ) : AnimatedColor() {

        init {
            prepare()
        }

        @Transient
        private val color: Color = value.toColor()

        override fun copy(): AnimatedColor {
            return Default(
                value = value,
                expression = expression,
                index = index
            )
        }

        override fun raw(state: AnimationState) = color
    }

    @Serializable
    class Animated(

        @SerialName("k")
        val value: List<VectorKeyframe>,

        @SerialName("x")
        override val expression: String? = null,

        @SerialName("ix")
        override val index: Int? = null
    ) : AnimatedColor(), RawKeyframeProperty<Color, VectorKeyframe> by BaseKeyframeAnimation(
        index = index,
        keyframes = value,
        emptyValue = Color.Transparent,
        map = { s, e, p ->
            lerp(s.toColor(), e.toColor(), easingX.transform(p))
        }
    ) {

        init {
            prepare()
        }

        override fun copy(): AnimatedColor {
            return Animated(
                value = value,
                expression = expression,
                index = index
            )
        }
    }
}

internal fun List<Float>.toColor() = Color(
    red = get(0).toColorComponent(),
    green = get(1).toColorComponent(),
    blue = get(2).toColorComponent(),
    alpha = getOrNull(3)?.toColorComponent() ?: 1f
)

internal fun List<Number>.toColor2() = Color(
    red = get(0).toFloat().toColorComponent(),
    green = get(1).toFloat().toColorComponent(),
    blue = get(2).toFloat().toColorComponent(),
    alpha = getOrNull(3)?.toFloat()?.toColorComponent() ?: 1f
)

// Modern Lotties (v 4.1.9+) have color components in the [0, 1] range.
// Older ones have components in the [0, 255] range.
private fun Float.toColorComponent() : Float = when (this) {
    in COLOR_RANGE_01 -> this
    in COLOR_RANGE_0255 -> this/255f
    else -> this // will likely throw error of invalid color space
}

private val COLOR_RANGE_01 = 0f..1f
private val COLOR_RANGE_0255 = 0f..255f


internal class AnimatedColorSerializer : JsonContentPolymorphicSerializer<AnimatedColor>(
    baseClass = AnimatedColor::class
) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AnimatedColor> {
        val k = requireNotNull(element.jsonObject["k"]) {
            "Animated shape must have 'k' parameter"
        }

        val animated = element.jsonObject["a"]?.jsonPrimitive?.intOrNull == 1 ||
                k is JsonArray && k[0] is JsonObject

        return if (animated) {
            AnimatedColor.Animated.serializer()
        } else {
            AnimatedColor.Default.serializer()
        }
    }

}

