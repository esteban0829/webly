package com.webClipBoard

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class ProjectNotFoundException : RuntimeException() {
}

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
class UnAuthorizedProjectException : RuntimeException() {
}

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class UserNotFoundException : RuntimeException() {
}

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class FolderNotFoundException : RuntimeException() {
}

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class LinkNotFoundException : RuntimeException() {
}

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class NotAllowedMoveToChildFolderException : RuntimeException() {
}
