import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.ExperimentalCompottieApi
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.dynamic.DynamicTextLayer
import io.github.alexzhirkevich.compottie.dynamic.rememberLottieDynamicProperties
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import io.github.alexzhirkevich.shared.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
public fun App() {
//    LottieFilesExample()
//    TestPlayground()
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            var introFinished by rememberSaveable { mutableStateOf(false) }
            if (!introFinished) {
                IntroAnimation(modifier = Modifier.fillMaxSize(), onFinished = { introFinished = true })
            }
            AnimatedVisibility(
                visible = introFinished,
                enter = fadeIn(),
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        LoopingAnimationGraphics(modifier = Modifier.fillMaxWidth())
                    }
                    LoopingAnimationText(modifier = Modifier.fillMaxWidth())
                    Button(onClick = {}, modifier = Modifier.align(alignment = Alignment.CenterHorizontally)) {
                        Text("Sign in")
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class, ExperimentalCompottieApi::class)
@Composable
private fun IntroAnimation(modifier: Modifier = Modifier, onFinished: () -> Unit) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/onboarding_part_1.json").decodeToString()
        )
    }
    val progress by animateLottieCompositionAsState(composition, iterations = 1)
    val topText = "Welcome to"
    val bottomText = "Compottie"
    val dynamicProperties = rememberLottieDynamicProperties {
        textLayer("**", "textTop") {
            fillColor {
                Color.White
            }
            text {
                topText
            }
            setLetterSpacing()
        }
        textLayer("**", "textBot") {
            fillColor {
                Color.White
            }
            text {
                bottomText
            }
            setLetterSpacing()
        }
        shapeLayer("**", "backgroundColor") {
            fill {
                color {
                    backgroundColor
                }
            }
        }
    }
    if (progress >= 1) {
        // Use DisposableEffect to avoid launching a coroutine with LaunchedEffect
        DisposableEffect(Unit) {
            onFinished()
            onDispose { }
        }
    }
    Image(
        modifier = modifier,
        contentScale = ContentScale.Crop,
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperties,
        ),
        contentDescription = "Intro animation",
    )
}

private fun DynamicTextLayer.setLetterSpacing() {
    tracking {
        -10f
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun LoopingAnimationGraphics(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(Res.readBytes("files/onboarding_part_2.json").decodeToString())
    }
    val progress by animateLottieCompositionAsState(composition, iterations = Compottie.IterateForever)
    Image(
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress }
        ),
        contentDescription = "Intro animation",
    )
}

@OptIn(ExperimentalResourceApi::class, ExperimentalCompottieApi::class)
@Suppress("SpreadOperator")
@Composable
private fun LoopingAnimationText(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition {
        LottieCompositionSpec.JsonString(
            Res.readBytes("files/onboarding_part_2_text.json").decodeToString()
        )
    }
    val progress by animateLottieCompositionAsState(composition, iterations = Compottie.IterateForever)
    val strings = listOf(
        "step1Top" to "Step #1",
        "step1Bot" to "Step #1",
        "step2Top" to "Step #2",
        "step2Bot" to "Step #2",
        "step3Top" to "Step #3",
        "step3Bot1" to "Step #3",
        "step3Bot2" to "Step #3",
        "step4Top" to "Step #4",
        "step4Bot" to "Step #4",
        "step5Top" to "Step #5",
        "step5Bot" to "Step #5",
    )
    val color = MaterialTheme.colorScheme.onSurface
    val dynamicProperties = rememberLottieDynamicProperties(color) {
        for (string in strings) {
            textLayer("**", string.first) {
                fillColor {
                    color
                }
                text {
                    string.second
                }
                setLetterSpacing()
            }
        }
    }
    Image(
        modifier = modifier,
        contentScale = ContentScale.FillWidth,
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress },
            dynamicProperties = dynamicProperties,
        ),
        contentDescription = "Intro animation",
    )
}
