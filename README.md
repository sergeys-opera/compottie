# Compottie

![badge-Android](https://img.shields.io/badge/Platform-Android-brightgreen)
![badge-iOS](https://img.shields.io/badge/Platform-iOS-lightgray)
![badge-JVM](https://img.shields.io/badge/Platform-JVM-orange)
![badge-macOS](https://img.shields.io/badge/Platform-macOS-purple)
![badge-web](https://img.shields.io/badge/Platform-Web-blue)

Compose Multiplatform Adobe After Effects Bodymovin (Lottie) animations rendering engine.

![Example](https://github.com/alexzhirkevich/compottie/assets/63979218/2735c77a-d940-4fd7-825c-208454b3960a)

# Installation

> [!IMPORTANT]
> Starting from v2.0 Compottie has its own multiplatform rendering engine without any platform delegates.
> <br>The new engine is implemented from scratch and therefore may have bugs.
> <br>Please [report](https://github.com/alexzhirkevich/compottie/issues) if you find any, preferably with a reproducible animation.
> <br>List of supported AE Lottie features can be found [here](/supported_features.md)

| Module | Description  | 
| :----: | ------------- | 
| `compottie`  | Main module with rendering engine and `JsonString` animation spec. Currently has two branches - 1.x (with platform renderers - Skottie and lottie-android) and 2.x (with own renderer). 1.x is maintained until the new renderer becomes stable  |
| `compottie⁠-⁠dot` | Contains [dotLottie](https://dotlottie.io/) and ZIP animation spec. For Compottie 2.x only |
| `compottie⁠-⁠network` | Contains `Url` animation spec and asset/font managers (with [Ktor](https://ktor.io/) and local cache with [Okio](https://square.github.io/okio/)). Allows loading animations and assets from web. For Compottie 2.x only |
| `compottie⁠-⁠resources` | Contains asset and font managers powered by official Compose resources. For Compottie 2.x only | 

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.alexzhirkevich/compottie/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.alexzhirkevich/compottie)

Add as a Gradle dependency
```kotlin
dependencies {
    implementation("io.github.alexzhirkevich:compottie:<version>")
    implementation("io.github.alexzhirkevich:compottie-dot:<version>")
    implementation("io.github.alexzhirkevich:compottie-network:<version>")
    implementation("io.github.alexzhirkevich:compottie-resources:<version>")
}
```

```toml
[versions]
compottie="<version>"

[libraries]
compottie = { module = "io.github.alexzhirkevich:compottie", version.ref = "compottie" }
compottie-dot = { module = "io.github.alexzhirkevich:compottie-dot", version.ref = "compottie" }
compottie-network = { module = "io.github.alexzhirkevich:compottie-network", version.ref = "compottie" }
compottie-resources = { module = "io.github.alexzhirkevich:compottie-resources", version.ref = "compottie" }
```

> [!NOTE]
For projects with Android minSdk < 26 `dot` and `network` modules require [desugaring](https://developer.android.com/studio/write/java8-support#library-desugaring)

# Usage
The following docs describe the Compottie 2.x usage.
For Compottie 1.x docs please refer to the [airbnb docs](https://github.com/airbnb/lottie/blob/master/android-compose.md#basic-usage).

## Compottie 2.0

- [Basic Usage](#basic-usage)
- [LottieComposition](#lottiecomposition)
- [Animating/Updating Progress](#animatingupdating-progress)
- [LottieAnimatable](#lottieanimatable)
- [dotLottie (ZIP)](#dotlottie-zip)
- [Images](#images)
- [Fonts](#fonts)
- [URL loading](#url-loading)
- [Dynamic Properties](#dynamic-properties)


## Basic Usage
```kotlin
val composition by rememberLottieComposition {
    LottieCompositionSpec.JsonString(
        Res.readBytes("files/anim.json").decodeToString()
    )
}
val progress by animateLottieCompositionAsState(composition)

Image(
    painter = rememberLottiePainter(
        composition = composition,
        progress = { progress },
    ),
    contentDescription = "Lottie animation"
)
```
Or with the `rememberLottiePainter` overload that merges `rememberLottiePainter` and `animateLottieCompositionsState()`
```kotlin
val composition by rememberLottieComposition {
    LottieCompositionSpec.JsonString(
        Res.readBytes("files/anim.json").decodeToString()
    )
}

Image(
    painter = rememberLottiePainter(
        composition = composition,
        iterations = Compottie.IterateForever
    ),
    contentDescription = "Lottie animation"
)
```

## LottieComposition
`LottieComposition` is the parsed version of your Lottie json file. It is stateless and can be 
cached/reused freely. Call `rememberLottieComposition(spec)` to create new composition. 
`LottieCompositionSpec` is an open interface that lets you select the animation source (string/zip, network/assets, etc.).

For example:
```kotlin
val animFromJsonRes by rememberLottieComposition {
    LottieCompositionSpec.JsonString(
        Res.readBytes("files/anim.json").decodeToString()
    )
}

val animFromUrl by rememberLottieComposition {
    LottieCompositionSpec.Url("https://example.com/anim.lotie")
}

val animFromArchiveRes by rememberLottieComposition {
    LottieCompositionSpec.DotLottie(
        Res.readBytes("files/anim.lottie")
    )
}
```

The type returned from `rememberLottieComposition` is
```kotlin
interface LottieCompositionResult : State<LottieComposition?>
```
This allows you to use it in two ways:
```kotlin
val composition: LottieComposition? by rememberLottieComposition(spec)
```
This will return null until the composition is parsed and then will return the `LottieComposition` object.
Use this version in most cases, especially if you don't need any of the extra functionality on `LottieCompositionResult`.

```kotlin
val compositionResult: LottieCompositionResult = rememberLottieComposition(spec)
```
`LottieCompositionResult` lets you:
1. Access the composition via `compositionResult.value`
2. Access `error`, `isLoading`, `isComplete`, `isFailure`, and `isSuccess` properties.
3. Call `await()` to await the parsed composition from a coroutine.

## Animating/Updating Progress

You have the option of handling progress entirely yourself. If you choose to do that, just pass in `progress` to your `LottieAnimation` composable.

In most cases, you will want to use either `animateLottieCompositionAsState()` or `LottieAnimatable`. These APIs were designed to be analogous to the standard Jetpack Compose APIs. `animateLottieCompositionAsState` is analogous to [animate*AsState](https://developer.android.com/jetpack/compose/animation#animatable) and `LottieAnimatable` is analogous to [Animatable](https://developer.android.com/jetpack/compose/animation#animatable).

The decision for whether to use one over the other is similar as well:
* If your animation is very simple or a function of other state properties, use `animateLottieCompositionAsState()`.
* If you need to imperatively call `animate` or `snapTo` from something like a `LaunchedEffect` then use `LottieAnimatable`.

`animateLottieCompositionAsState()` returns and `LottieAnimatable` implements:
```kotlin
interface LottieAnimationState : State<Float>
```

### animateLottieCompositionAsState()
```kotlin
val progress by animateLottieCompositionAsState(composition)
```
```kotlin
val progress by animateLottieCompositionAsState(
    composition,
    iterations = Compottie.IterateForever,
)
```
```kotlin
val progress by animateLottieCompositionAsState(
    composition,
    clipSpec = LottieClipSpec.Progress(0.5f, 0.75f),
)
```

### LottieAnimatable
```kotlin
@Stable
class MyHoistedState {
    val lottieAnimatable = LottieAnimatable()
    val somethingElse by mutableStateOf(0f)
}
```
```kotlin
val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation))
val lottieAnimatable = rememberLottieAnimatable()
LaunchedEffect(Unit) {
    lottieAnimatable.animate(
        composition,
        iterations = Compottie.IterateForever,
        clipSpec = LottieClipSpec.Progress(0.5f, 0.75f),
    )
}
```

## dotLottie (ZIP)

[dotLottie ](https://dotlottie.io/) is an open-source file format that aggregates one or more Lottie files and their associated resources into a single file. They are ZIP archives compressed with the Deflate compression method and carry the file extension of ".lottie".

dotLottie animations are up to 10x smaller in size and can have auto-linked bundled assets (as well as external assets ofc).

`compottie-dot` module is required to use dotLottie animations in your app. It brings the new type of composition spec - `LottieCompositionSpec.DotLottie`.

You can also use `DotLottie` composition spec even without making a .lottie file just by ZIPping
your animation with assets using deflate algorithm. The only limitation - animation can't be named
as "manifest.json" and should contain exactly one .json file.

The `DotLottie` composition spec works both with dotLottie and simple ZIP files.
For dotLottie files you can specify the animation to run if archive contains multiple animations

```kotlin
fun LottieCompositionSpec.Companion.DotLottie(
    archive: ByteArray,
    animationId: String?
) : LottieCompositionSpec
```

## Images

Images should be avoided whenever possible. They are much larger, less performant, and can lead to pixelation. Whenever possible, try and make your animation consist solely of vectors. However, Lottie does support images in one of 4 ways:
1. Baked into the Lottie json file. This is done via an option in the exporter (such as teh Bodymovin After Effects plugin). When done, images are encoded as a base64 string and embedded directly in the json file. This is the simplest way to use images because everything is contained in a single file and no additional work is necessary to make them work;
2. Zipped with the json file in a single zip file. When parsing the animation, Lottie will unzip the animation and automatically link any images in zip file to the composition. These zip files can be stored in assets and loaded via `LottieCompositionSpec.DotLottie` (requires `compottie-dot` module) or downloaded via the internet and loaded via `LottieCompositionSpec.Url`;
3. External images provided by `LottieAssetsManager`;
4. Via dynamic properties.

`LottieAssetsManager` should be passed to `rememberLottieComposition` to load external resources.
`compottie-resources` provides ready-to-use implementation that loads assets from compose-resources:

```kotlin
val painter = rememberLottiePainter(
    composition = composition,
    assetsManager = rememberResourcesAssetsManager(
        directory = "files" // by default,
        readBytes = Res::readBytes
    )
)
```

## Fonts

Text can be drawn in 2 ways: using fonts and using glyphs (when characters are baked to the animation as lottie shapes)

`LottieFontManager` should be passed to `rememberLottiePainter` to use custom fonts.
`compottie-resources` provides ready-to-use implementation that loads fonts from compose-resources:

```kotlin
val painter = rememberLottiePainter(
    composition = composition,
    fontManager = rememberResourcesFontManager { fontSpec ->
        when (fontSpec.family) {
            "Comic Neue" -> Res.font.ComicNeue
            else -> null // default font will be used
        }
    }
)
```

## URL loading
To load images remotely `compottie-network` module should be added as a dependensy.
This module brings an additional composition spec called `LottieCompositionSpec.Url`
```kotlin
fun LottieCompositionSpec.Companion.Url(
    url : String,
    format: LottieAnimationFormat,
    client: HttpClient,
    request : NetworkRequest,
    cacheStrategy: LottieCacheStrategy,
)
```
that can be used to load JSON and dotLottie animations from the Internet.

`LottieAnimationFormat` is used to determine wheither animation is a JSON or dotLottie. If you left it `Undefined`,
composition spec will automatically detect is this a JSON or dotLottie file.

Ktor HTTP client can be provided with `client` parameter.

Caching strategy can be set with `cacheStrategy` parameter. By default animations are cached in the
device temp directory.

The network module also brings the `NetworkAssetsManager` that have similar parameters and can be used to load image assets.
If you are using Url composition spec then specifying `NetworkAssetsManager` is redundant.
Url composition spec automatically prepares url assets

There is no stable Ktor client for wasm so to use network module on this target you need to add
the following to the bottom of your build script:

```kotlin
configurations
    .filter { it.name.contains("wasmJs") }
    .onEach {
        it.resolutionStrategy.eachDependency {
            if (requested.group.startsWith("io.ktor") &&
                requested.name.startsWith("ktor-client-")
            ) {
                useVersion("3.0.0-wasm2")
            }
        }
    }
```
## Dynamic Properties

Lottie allows you to update animation properties at runtime. Some reasons you may want to do this are:
1. Change colors for day/night or other app theme.
2. Localize animation text
3. Change the progress of a specific layer to show download progress.
4. Change the size and position of something in response to a gesture.

Dynamic properties are created with `rememberLottieDynamicProperties`

```kotlin
val painter = rememberLottiePainter(
    composition = composition,
    dynamicProperties = rememberLottieDynamicProperties {
        shapeLayer("Precomposition 1", "Shape Layer 4") {
            transform {
                rotation { current -> current * progress }
            }
            fill("Group 1", "Fill 4") {
                color { Color.Red }
                alpha { .5f }
            }
            group("Group 4") {
                ellipse("Ellipse 1") {
                    // configure size, position of the ellipse named "Ellipse 1"
                }
                stroke("Ellipse 1 Stroke") {
                    // configure stroke named "Ellipse 1 Stroke" in the same group
                }
            }
        }
    }
) 
```

Note, that final property building blocks (such as rotations, color, alpha) are called on EACH ANIMATION FRAME and should be cached if they don't rely on progress and have allocations or hard computations.

More info about dynamic properties for those who is not familiar with AE / Lottie format can be found [here](/dynamic_properties.md).
