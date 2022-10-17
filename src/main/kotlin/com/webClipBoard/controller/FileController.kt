package com.webClipBoard.controller

import com.webClipBoard.service.FileService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class FileController(
    private val fileService: FileService,
) {

    @ResponseBody
    @PostMapping("/api/presigned-url")
    fun preSignedUrl(
        @RequestBody filename: String
    ): String {
        return fileService.createPresignedUrl(filename)
    }

}