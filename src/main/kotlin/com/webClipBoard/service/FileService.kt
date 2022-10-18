package com.webClipBoard.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.webClipBoard.config.S3BucketType
import com.webClipBoard.config.S3Config
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class FileService(
    private val amazonS3: AmazonS3,
    private val s3Config: S3Config,
) {

    @Transactional
    fun createPresignedUrl(filename: String): String {
        val bucketName = s3Config.buckets[S3BucketType.WEB_CLIPBOARD]

        val expiration = Date()
        val oneHour = (1000 * 60 * 60).toLong()
        expiration.time = Instant.now().plusMillis(oneHour).toEpochMilli()

        val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
            GeneratePresignedUrlRequest(bucketName, filename)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString()
    }
}