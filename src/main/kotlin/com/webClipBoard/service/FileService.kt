package com.webClipBoard.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.webClipBoard.FileEntity
import com.webClipBoard.FileRepository
import com.webClipBoard.config.S3BucketType
import com.webClipBoard.config.S3Config
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.time.Instant
import java.util.*

@Service
class FileService(
    private val amazonS3: AmazonS3,
    private val s3Config: S3Config,
    private val fileRepository: FileRepository,
) {

    fun createPresignedUrl(filename: String): URL {
        val bucket = s3Config.buckets[S3BucketType.WEB_CLIPBOARD]
        val expiration = Date().apply {
            val oneHour = (1000 * 60 * 60).toLong()
            time = Instant.now().plusMillis(oneHour).toEpochMilli()
        }

        return GeneratePresignedUrlRequest(bucket, filename)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)
                .let { amazonS3.generatePresignedUrl(it) }
    }

    @Transactional
    fun createFileAndReturnPresignedUrl(filename: String) = createPresignedUrl(filename).also {
        fileRepository.save(FileEntity(
            name = filename,
            filePath = it.path,
        ))
    }.run {
        toString()
    }
}
