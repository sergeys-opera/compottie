package io.github.alexzhirkevich.compottie.internal.layers

import androidx.compose.ui.geometry.MutableRect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.IntSize
import io.github.alexzhirkevich.compottie.internal.helpers.LottieBlendMode
import io.github.alexzhirkevich.compottie.internal.helpers.Transform
import io.github.alexzhirkevich.compottie.internal.helpers.BooleanInt
import io.github.alexzhirkevich.compottie.internal.helpers.MatteMode
import io.github.alexzhirkevich.compottie.internal.assets.LottieAsset
import io.github.alexzhirkevich.compottie.internal.helpers.Mask
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.roundToInt

@Serializable
@SerialName("2")
internal class ImageLayer(
    @SerialName("ks")
    override val transform: Transform = Transform(),

    @SerialName("ao")
    override val autoOrient: BooleanInt = BooleanInt.No,

    @SerialName("ddd")
    override val is3d: BooleanInt = BooleanInt.No,

    @SerialName("ind")
    override val index: Int? = null,

    @SerialName("bm")
    override val blendMode: LottieBlendMode = LottieBlendMode.Normal,

    @SerialName("cl")
    override val clazz: String? = null,

    @SerialName("ln")
    override val htmlId: String? = null,

    @SerialName("ip")
    override val inPoint: Float? = null,

    @SerialName("op")
    override val outPoint: Float? = null,

    @SerialName("st")
    override val startTime: Int? = null,

    @SerialName("nm")
    override val name: String? = null,

    @SerialName("sr")
    override val timeStretch: Float = 1f,

    @SerialName("parent")
    override val parent: Int? = null,

    @SerialName("tt")
    override val matteMode: MatteMode? = null,

    @SerialName("tp")
    override val matteParent: Int? = null,

    @SerialName("td")
    override val matteTarget: BooleanInt? = null,

    @SerialName("hd")
    override val hidden: Boolean = false,

    @SerialName("ct")
    override val collapseTransform: BooleanInt = BooleanInt.No,

    @SerialName("masksProperties")
    override val masks: List<Mask>? = null,

    @SerialName("refId")
    val refId : String,

    ) : BaseLayer(), VisualLayer {

    @Transient
    private val src = MutableRect(0f,0f,0f,0f)

    @Transient
    private val dst = MutableRect(0f,0f,0f,0f)

    @Transient
    private val paint = Paint()

    private val asset : LottieAsset.ImageAsset? by lazy {
        assets[refId] as? LottieAsset.ImageAsset
    }

    override fun drawLayer(drawScope: DrawScope, parentMatrix: Matrix, parentAlpha: Float, frame: Float) {
        val mAsset = asset ?: return
        val bitmap = mAsset.bitmap ?: return

        paint.alpha = parentAlpha

        drawScope.drawIntoCanvas { canvas ->

            canvas.save()
            canvas.concat(parentMatrix)
            src.set(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

            val dstSize = if (maintainOriginalImageBounds) {
                IntSize(
                    (mAsset.width * drawScope.density).roundToInt(),
                    (mAsset.height * drawScope.density).roundToInt()
                )
            } else {
                IntSize(
                    (bitmap.width * drawScope.density).roundToInt(),
                    (bitmap.height * drawScope.density).roundToInt()
                )
            }

            val srcSize = IntSize(
                (bitmap.width * drawScope.density).toInt(),
                (bitmap.height * drawScope.density).toInt()
            )

            canvas.drawImageRect(
                bitmap,
                srcSize = srcSize,
                dstSize = dstSize,
                paint = paint
            )
            canvas.restore()
        }
    }

    override fun getBounds(
        outBounds: MutableRect,
        parentMatrix: Matrix,
        applyParents: Boolean,
        frame: Float
    ) {
        super.getBounds(outBounds, parentMatrix, applyParents, frame)

        asset?.let {
            outBounds.set(0f, 0f, it.width * density, it.height * density)
            boundsMatrix.map(outBounds)
        }
    }
}

