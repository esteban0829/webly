package com.webClipBoard.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class FileService(
    private val amazonS3: AmazonS3
) {

    @Transactional
    fun createPreassignedUrl(filename: String): String {
        val bucketName = "web-clipboard"

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