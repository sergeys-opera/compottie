package io.github.alexzhirkevich.compottie.internal.animation.expressions.operations

import io.github.alexzhirkevich.compottie.internal.AnimationState
import io.github.alexzhirkevich.compottie.internal.animation.expressions.Operation

internal object OpGetTime : Operation {
    override fun invoke(value: Any, variables: MutableMap<String, Any>, state: AnimationState): Any {
        return state.time
    }
}