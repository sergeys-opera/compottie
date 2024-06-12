package io.github.alexzhirkevich.compottie.internal.animation

import androidx.compose.ui.graphics.Matrix
import io.github.alexzhirkevich.compottie.internal.AnimationState
import io.github.alexzhirkevich.compottie.internal.utils.preRotate
import io.github.alexzhirkevich.compottie.internal.utils.preScale
import io.github.alexzhirkevich.compottie.internal.utils.preTranslate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.pow

@Serializable
internal class RepeaterTransform(
    @SerialName("a")
    override val anchorPoint : AnimatedVector2? = null,

    @SerialName("p")
    override val position : AnimatedVector2? = null,

    @SerialName("s")
    override val scale : AnimatedVector2? = null,

    @SerialName("r")
    override val rotation : AnimatedNumber? = null,

    @SerialName("o")
    override val opacity : AnimatedNumber? = null,

    @SerialName("sk")
    override val skew: AnimatedNumber? = null,

    @SerialName("sa")
    override val skewAxis: AnimatedNumber? = null,

    @SerialName("so")
    val startOpacity : AnimatedNumber? = null,

    @SerialName("eo")
    val endOpacity : AnimatedNumber? = null,
) : AnimatedTransform() {

    fun repeaterMatrix(state: AnimationState, amount: Float): Matrix {
        matrix.reset()

        position?.interpolated(state)?.let {
            matrix.preTranslate(
                it.x * amount,
                it.y * amount
            )
        }

        scale?.interpolatedNorm(state)?.takeIf {
            it.x != 1f || it.y != 1f
        }?.let {
            matrix.preScale(
                it.x.pow(amount),
                it.y.pow(amount)
            )
        }

        rotation?.interpolated(state)?.let {
            val anchorPoint = anchorPoint?.interpolated(state)

            if (anchorPoint != null) {
                matrix.translate(anchorPoint.x, anchorPoint.y)
            }
            matrix.preRotate(it * amount)

            if (anchorPoint != null) {
                matrix.translate(-anchorPoint.x, -anchorPoint.y)
            }
        }

        return matrix
    }
}