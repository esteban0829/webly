package com.webClipBoard.file

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class FileService(
    private val amazonS3: AmazonS3
) {

    fun createPresignedUrl(filename: String): String {
        val bucketName = "web-clipboard"

        val expiration = Date()
        val oneHour = (1000 * 60 * 60).toLong();
        expiration.time = Instant.now().plusMillis(oneHour).toEpochMilli();

        println("Generating pre-signed URL.")
        val generatePresignedUrlRequest: GeneratePresignedUrlRequest =
            GeneratePresignedUrlRequest(bucketName, filename)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration)

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest).toString();
    }
}