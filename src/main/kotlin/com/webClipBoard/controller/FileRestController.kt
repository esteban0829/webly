package com.webClipBoard.controller

import com.webClipBoard.FileCreateDTO
import com.webClipBoard.FileDTO
import com.webClipBoard.FileStatus
import com.webClipBoard.FileUserDTO
import com.webClipBoard.service.FileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/files")
class FileRestController(
    private val fileService: FileService,
) {

    @PostMapping("/createPresignedUrl")
    fun createPresignedUrl(
        @RequestBody filename: String
    ): ResponseEntity<String> = ResponseEntity
            .status(HttpStatus.CREATED)
            .body(fileService.createFileAndReturnPresignedUrl(filename))

    @PostMapping("")
    fun createFile(
        @RequestBody fileCreateDTO: FileCreateDTO
    ): ResponseEntity<FileUserDTO> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(fileService.createFile(fileCreateDTO))
    }

    @PatchMapping("/{fileId}/{fileStatus}")
    fun updateFileStatus(
        @PathVariable("fileId") fileId: Long,
        @PathVariable("fileStatus") fileStatus: FileStatus,
    ): ResponseEntity<FileDTO> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(fileService.updateFileStatus(fileId, fileStatus))
    }
}
