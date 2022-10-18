package com.webClipBoard.controller

import com.webClipBoard.service.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/files")
class FileRestController(
    private val fileService: FileService,
) {

    @Transactional
    @PostMapping("/createPresignedUrl")
    fun createPresignedUrl(
        @RequestBody filename: String
    ): ResponseEntity<String> {
        return ResponseEntity(
            fileService.createPresignedUrl(filename),
            HttpStatus.CREATED,
        )
    }

}