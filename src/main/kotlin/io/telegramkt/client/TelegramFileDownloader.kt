package io.telegramkt.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.readAvailable
import io.telegramkt.api.TelegramResponse
import io.telegramkt.exception.TelegramApiException
import io.telegramkt.model.file.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File as JavaFile
import java.io.FileOutputStream

class TelegramFileDownloader(
    private val httpClient: HttpClient,
    private val token: String,
    private val apiUrl: String,
) {

    suspend fun download(
        fileId: String,
        destination: JavaFile = JavaFile("./downloads"),
        fileName: String? = null,
    ): JavaFile = withContext(Dispatchers.IO) {
        val fileInfo = httpClient.get("$apiUrl/bot$token/getFile") {
            parameter("file_id", fileId)
        }.body<TelegramResponse<File>>()

        fileInfo.requireSuccess()
        val filePath = fileInfo.result?.filePath
            ?: throw TelegramApiException(-1, "filePath is null for fileId: $fileId")

        downloadByPath(filePath, destination, fileName)
    }

    suspend fun downloadByPath(
        filePath: String,
        destination: JavaFile = JavaFile("./downloads"),
        fileName: String? = null,
    ): JavaFile = withContext(Dispatchers.IO) {
        if (!destination.exists()) {
            destination.mkdirs()
        }

        val finalFileName = fileName ?: filePath.substringAfterLast('/')
        val outputFile = JavaFile(destination, finalFileName)
        val fileUrl = "$apiUrl/file/bot$token/$filePath"
        val channel = httpClient.get(fileUrl).bodyAsChannel()

        FileOutputStream(outputFile).use { outputStream ->
            val buffer = ByteArray(8192)
            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer)
                if (bytesRead <= 0) break
                outputStream.write(buffer, 0, bytesRead)
            }
        }

        outputFile
    }

    suspend fun downloadBytes(fileId: String): ByteArray = withContext(Dispatchers.IO) {
        val fileInfo = httpClient.get("$apiUrl/bot$token/getFile") {
            parameter("file_id", fileId)
        }.body<TelegramResponse<File>>()

        fileInfo.requireSuccess()
        val filePath = fileInfo.result?.filePath
            ?: throw TelegramApiException(-1, "filePath is null for fileId: $fileId")

        val fileUrl = "$apiUrl/file/bot$token/$filePath"
        httpClient.get(fileUrl).body<ByteArray>()
    }
}