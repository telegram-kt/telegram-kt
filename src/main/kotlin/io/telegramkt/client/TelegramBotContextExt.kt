package io.telegramkt.client

import io.telegramkt.dsl.BotContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File as JavaFile

suspend fun BotContext.downloadFile(
    destination: JavaFile = JavaFile("./downloads"),
    fileName: String? = null,
): JavaFile? = withContext(Dispatchers.IO) {
    val fileId = message?.photo?.maxByOrNull { it.fileSize }?.fileId
        ?: message?.document?.fileId
        ?: message?.audio?.fileId
        ?: message?.video?.fileId
        ?: message?.voice?.fileId
        ?: message?.animation?.fileId
        ?: message?.videoNote?.fileId
        ?: message?.sticker?.fileId
        ?: return@withContext null

    val downloader = TelegramFileDownloader(client.httpClient, client.token, client.apiUrl)
    downloader.download(fileId, destination, fileName ?: "file_${message!!.id}")
}