import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.NoLiveLiterals
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import compottie.example.shared.generated.resources.ComicNeue
import compottie.example.shared.generated.resources.Res
import io.github.alexzhirkevich.compottie.CompottieException
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.LottieClipSpec
import io.github.alexzhirkevich.compottie.LottieComposition
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.LottieConstants
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import io.github.alexzhirkevich.compottie.rememberResourcesAssetsManager
import io.github.alexzhirkevich.compottie.rememberResourcesFontManager
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi

private val GRADIENT_ELLIPSE = "gradient_ellipse.json"
private val TEST = "test.json"
private val CHECKMARK = "checkmark.json"
private val FADE_BALLS = "fade_balls.json"
private val BOUNCING_BALL = "bouncing_ball.json"
private val POLYSTAR = "polystar.json"
private val RECT = "rect.json"
private val ROUND_RECT = "roundrect.json"
private val ROBOT = "robot.json"
private val ROBOT_404 = "robot_404.json"
private val ASTRONAUT = "astronaut.json"
private val ANGEL = "angel.json"
private val CONFETTI = "confetti.json"
private val WONDERS = "wonders.json"
private val PRECOMP_WITH_REMAPPING = "precomp_with_remapping.json"
private val MASK_ADD = "mask_add.json"
private val MATTE_LUMA = "luma_matte.json"
private val DASH = "dash.json"
private val ROUNDING_CORENERS = "rounding_corners.json"
private val REPEATER = "repeater.json"
private val AUTOORIENT = "autoorient.json"
private val TEXT_WITH_PATH = "text_with_path.json"
private val TEXT = "text.json"
private val TEXT_GLYPHS = "text_glyphs.json"
private val TEXT_OFFSET = "text_offset.json"
private val IMAGE_ASSET = "image_asset.json"
private val IMAGE_ASSET_EMBEDDED = "image_asset_embedded.json"

private val DOT = "dotlottie/dot.lottie"
private val DOT_WITH_IMAGE = "dotlottie/dot_with_image.lottie"

private val ALL = listOf(
    GRADIENT_ELLIPSE,
    TEST,
    CHECKMARK,
    FADE_BALLS,
    BOUNCING_BALL,
    POLYSTAR,
    RECT,
    ROUND_RECT,
    ROBOT,
    ROBOT_404,
    ASTRONAUT,
    ANGEL,
    CONFETTI,
    WONDERS,
    PRECOMP_WITH_REMAPPING,
    MASK_ADD,
    MATTE_LUMA,
    DASH,
    ROUNDING_CORENERS,
    REPEATER,
    AUTOORIENT,
    TEXT_WITH_PATH,
    TEXT,
    TEXT_GLYPHS,
    TEXT_OFFSET,
    IMAGE_ASSET,
    IMAGE_ASSET_EMBEDDED,
)


/**
 * [LottieComposition] spec from composeResources/[dir]/[path] json asset
 * */
@OptIn(ExperimentalResourceApi::class)
@Stable
suspend fun LottieCompositionSpec.Companion.Resource(
    path : String,
    dir : String = "files",
    readBytes: suspend (path: String) -> ByteArray = { Res.readBytes(it) }
) : LottieCompositionSpec = JsonString(readBytes("$dir/$path").decodeToString())

@OptIn(ExperimentalResourceApi::class, ExperimentalCompottieApi::class)
@Composable
fun App() {

//    return LottieFontExample()
//    return AllExamples()

    val composition = rememberLottieComposition(
        assetsManager = rememberResourcesAssetsManager(
            readBytes = Res::readBytes
        ),
        fontManager = rememberResourcesFontManager { fontSpec ->
            when (fontSpec.family) {
                "Comic Neue" -> Res.font.ComicNeue
                else -> null
            }
        },
    ) {
//        LottieCompositionSpec.DotLottie(ResourcesAssetsManager()) {
//            Res.readBytes("files/$DOT_WITH_IMAGE")
//        }
        LottieCompositionSpec.Resource(ROBOT)
//
//        LottieCompositionSpec.Url(
//            "https://assets-v2.lottiefiles.com/a/1633f0d6-117a-11ee-a98b-eb5ca344688a/8OoA7VFval.lottie",
//            "https://assets-v2.lottiefiles.com/a/10956594-1169-11ee-98fe-ef3d9d71ad0f/WVFg2bDWGj.lottie",
//            "https://assets-v2.lottiefiles.com/a/0e63252e-1153-11ee-9e35-dfc2b798a135/sYygPbem7R.lottie",
//            "https://github.com/airbnb/lottie-android/raw/master/snapshot-tests/src/main/assets/Tests/dalek.json",
//            "https://dotlottie.io/sample_files/animation-external-image.lottie",
//            "https://github.com/airbnb/lottie-android/raw/master/snapshot-tests/src/main/assets/Tests/august_view_pulse.zip",
//            "https://github.com/airbnb/lottie-android/raw/master/snapshot-tests/src/main/assets/Tests/anim_jpg.zip",
//            "https://github.com/airbnb/lottie-android/raw/master/snapshot-tests/src/main/assets/Tests/ZipInlineImage.zip",
//        )
    }

    // If you want to be aware of loading errors
    LaunchedEffect(composition) {
        try {
            composition.await()
        } catch (t : CompottieException){
            t.printStackTrace()
        }
    }

    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val progress by animateLottieCompositionAsState(
//            clipSpec = LottieClipSpec.Progress(.55f, 1f),
            iterations = LottieConstants.IterateForever,
            composition = composition.value
        )
        val painter  = rememberLottiePainter(
            composition = composition.value,
            progress = { progress },
//            clipToCompositionBounds = false
        )

        Image(
            modifier = Modifier
                .fillMaxSize()
                .opacityGrid()
            ,painter = painter,
            contentDescription = null
        )

        if (composition.value == null) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalResourceApi::class, ExperimentalCompottieApi::class)
@Composable
fun AllExamples(){
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .opacityGrid(),
        columns = GridCells.FixedSize(150.dp),
    ){
        items(ALL) {
            val composition by rememberLottieComposition(
                assetsManager = rememberResourcesAssetsManager(
                    readBytes = Res::readBytes
                ),
                fontManager = rememberResourcesFontManager { fontSpec ->
                    when (fontSpec.family) {
                        "Comic Neue" -> Res.font.ComicNeue
                        else -> null
                    }
                },
            ) {
                LottieCompositionSpec.Resource(it)
            }

            Image(
                painter = rememberLottiePainter(composition, iterations = LottieConstants.IterateForever),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .border(1.dp, Color.Black )
            )
        }
    }
}

private val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ':, \n"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LottieFontExample() {
    var text by remember {
        mutableStateOf("")
    }

    val add1 = "COMPOTTIE NOW HAS IT'S OWN  COMPOSE MULTIPLATFORM LOTTIE RENDERING ENGINE"

    LaunchedEffect(0) {
//        while (true) {
        listOf(add1).forEach { line ->
            line.forEach {
//                if (it == ' ') {
//                    delay(200)
//                } else {
                delay(30)
//                }
                text += it
            }
            delay(500)
        }
//            delay(1000)
//            text = ""
//        }
    }

    val fontSize = 90.dp

    val focus = remember {
        FocusRequester()
    }
    LaunchedEffect(focus) {
        focus.requestFocus()
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .focusRequester(focus),
//            .focusable(interactionSource = interactionSource),
        contentAlignment = Alignment.Center
    ) {


        BasicTextField(
            modifier = Modifier.fillMaxSize(),
            value = text,
            onValueChange = {
                text = it
            },
            decorationBox = {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FlowRow(
                        modifier = Modifier
                            .animateContentSize()
                            .padding(100.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center
                    ){
                        text.split(" ").forEach { word ->
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = -fontSize / 3,
                                    alignment = Alignment.CenterHorizontally
                                ),
                            ) {
                                word.forEach { c ->
                                    val anim = when (c) {

                                        ':' -> "Colon"
                                        ',' -> "Comma"
                                        '\'' -> "Apostrophe"
                                        else -> c.toString().uppercase()
                                    }
                                    Image(
                                        modifier = Modifier
                                            .height(fontSize)
                                            .width(fontSize * 3 / 4f),
                                        painter = rememberLottiePainter(
                                            rememberLottieComposition {
                                                // sometimes cmp resources freeze on simultaneous resources access
                                                LottieCompositionSpec.Resource("mobilo/$anim.json")
                                            }.value
                                        ),
                                        contentDescription = anim
                                    )
                                }
                            }
                        }

                        Image(
                            modifier = Modifier
                                .height(fontSize)
                                .padding(fontSize / 4)
                                .offset(x = -fontSize / 3),
                            painter = rememberLottiePainter(
                                composition = rememberLottieComposition {
                                    LottieCompositionSpec.Resource("mobilo/BlinkingCursor.json")
                                }.value,
                                iterations = LottieConstants.IterateForever
                            ),
                            contentDescription = it.toString()
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun LottieList() {
    LazyVerticalGrid(columns = GridCells.FixedSize(100.dp)) {
        items(1000){
            val composition = rememberLottieComposition(
                key = "ROBOT"
            ) {
                LottieCompositionSpec.Resource(ROBOT)
//                LottieCompositionSpec.Url(
//                    url = "https://github.com/airbnb/lottie-android/raw/master/snapshot-tests/src/main/assets/Tests/dalek.json",
//                )
            }

            val painter = rememberLottiePainter(
                composition.value,
                iterations = LottieConstants.IterateForever
            )

            Image(
                modifier = Modifier.height(100.dp),
                painter = painter,
                contentDescription = ""
            )
        }
    }
}

private val DarkOpacity = Color(0xff7f7f7f)
private val LightOpacity = Color(0xffb2b2b2)
private fun Modifier.opacityGrid(cellSize : Dp = 30.dp) = drawBehind {

    val sizePx = cellSize.toPx()
    val s = Size(sizePx,sizePx)
    repeat((size.width /sizePx).toInt() + 1){ i ->
        repeat((size.height / sizePx).toInt() + 1){ j->

            drawRect(
                color = if (i % 2 ==  j % 2) DarkOpacity else LightOpacity,
                topLeft = Offset(i * sizePx, j * sizePx),
                size = s
            )
        }
    }
}
