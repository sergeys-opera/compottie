package io.github.alexzhirkevich.compottie.internal.animation.expressions.operations.composition

import io.github.alexzhirkevich.compottie.internal.animation.expressions.Expression
import io.github.alexzhirkevich.compottie.internal.animation.expressions.ExpressionComposition
import io.github.alexzhirkevich.compottie.internal.animation.expressions.ExpressionContext
import io.github.alexzhirkevich.compottie.internal.animation.expressions.argAt
import io.github.alexzhirkevich.compottie.internal.animation.expressions.checkArgs

internal sealed class OpCompositionContext : ExpressionContext<ExpressionComposition> {

    final override fun interpret(
        callable: String?,
        args: List<Expression>?
    ): Expression? {
        return when (callable) {
            "numLayers" -> withContext { _, _, _ -> layersCount }
            "width" -> withContext { _, _, _ -> width }
            "height" -> withContext { _, _, _ -> height }
            "displayStartTime" -> withContext { _, _, _ -> startTime }
            "frameDuration" -> withContext { _, _, s -> s.composition.frameRate / 1000 }
            "layer" -> {
                checkArgs(args, 1, callable)
                OpGetLayer(
                    comp = this,
                    nameOrIndex = args.argAt(0)
                )
            }

            else -> null
        }
    }
}