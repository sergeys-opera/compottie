package io.github.alexzhirkevich.compottie

import io.ktor.client.HttpClient
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLParserException
import io.ktor.http.Url
import io.ktor.util.toByteArray
import kotlinx.coroutines.withContext
import okio.Path

@OptIn(InternalCompottieApi::class)
private val NetworkLock = MapMutex()

@OptIn(InternalCompottieApi::class)
internal suspend fun networkLoad(
    client: HttpClient,
    cacheStrategy: LottieCacheStrategy,
    request : NetworkRequest,
    url: String
): Pair<Path?, ByteArray?> {
    return withContext(ioDispatcher()) {
        NetworkLock.withLock(url) {
            try {
                try {
                    cacheStrategy.load(url)?.let {
                        return@withLock cacheStrategy.path(url) to it
                    }
                } catch (_: Throwable) {
                }

                val ktorUrl = try {
                    Url(url)
                } catch (t: URLParserException) {
                    return@withLock null to null
                }

                val bytes = request(client, ktorUrl).execute().bodyAsChannel().toByteArray()

                try {
                    cacheStrategy.save(url, bytes)?.let {
                        return@withLock it to bytes
                    }
                } catch (e: Throwable) {
                    Compottie.logger?.error(
                        "${this::class.simpleName} failed to cache downloaded asset",
                        e
                    )
                }
                null to bytes
            } catch (t: Throwable) {
                null to null
            }
        }
    }
}