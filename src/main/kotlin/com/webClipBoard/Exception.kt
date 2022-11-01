package com.webClipBoard

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class ProjectNotFoundException : RuntimeException() {
}

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
class UnAuthorizedProjectException : RuntimeException() {
}
