package com.webClipBoard.service

import com.amazonaws.HttpMethod
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.webClipBoard.*
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

    private fun createPresignedUrl(filename: String): URL {
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
        fileRepository.save(File(
            name = filename,
            filePath = it.path,
        ))
    }.run {
        toString()
    }

    @Transactional
    fun createFile(fileCreateDTO: FileCreateDTO): FileUserDTO {
        val presignedUrl = createPresignedUrl(fileCreateDTO.fileName)
        return fileCreateDTO.run {
            fileRepository.save(File(
                name = fileName,
                filePath = presignedUrl.path
            )).toDTO().toFileUserDTO(presignedUrl = presignedUrl.toString())
        }
    }

    @Transactional
    fun updateFileStatus(fileId: Long, status: FileStatus): FileDTO {
        val file = fileRepository.findByIdForUpdate(fileId)
        file.status = status
        return fileRepository.save(file).toDTO()
    }

    private fun FileDTO.toFileUserDTO(presignedUrl: String): FileUserDTO {
        return FileUserDTO(
            id = id,
            name = name,
            filePath = filePath,
            status = status,
            createDateTime = createDateTime,
            updateDateTime = updateDateTime,
            presignedUrl = presignedUrl,
        )
    }
}
