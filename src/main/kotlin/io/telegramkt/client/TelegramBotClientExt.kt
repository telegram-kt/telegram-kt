package io.telegramkt.client

import java.io.File as JavaFile

suspend fun TelegramBotClient.downloadFile(
    fileId: String,
    destination: JavaFile = JavaFile("./downloads"),
    fileName: String? = null,
): JavaFile {
    val downloader = TelegramFileDownloader(httpClient, token, apiUrl)
    return downloader.download(fileId, destination, fileName)
}

suspend fun TelegramBotClient.downloadFileByPath(
    filePath: String,
    destination: JavaFile = JavaFile("./downloads"),
    fileName: String? = null,
): JavaFile {
    val downloader = TelegramFileDownloader(httpClient, token, apiUrl)
    return downloader.downloadByPath(filePath, destination, fileName)
}

suspend fun TelegramBotClient.downloadFileBytes(fileId: String): ByteArray {
    val downloader = TelegramFileDownloader(httpClient, token, apiUrl)
    return downloader.downloadBytes(fileId)
}