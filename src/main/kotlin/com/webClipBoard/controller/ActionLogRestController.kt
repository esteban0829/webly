package com.webClipBoard.controller

import com.webClipBoard.Account
import com.webClipBoard.ActionLogDTO
import com.webClipBoard.service.ActionLogService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.websocket.server.PathParam

@RestController
@RequestMapping("/api/v1/projects/{projectId}/action-logs")
class ActionLogRestController(
    private val actionLogService: ActionLogService,
) {

    @GetMapping
    fun getActionLog(
        @PathVariable projectId: Long,
        @PathParam("recentReadActionLogId") recentReadActionLogId: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<List<ActionLogDTO>> {
        return ResponseEntity
            .ok()
            .body(actionLogService.getActionLogs(account, projectId, recentReadActionLogId))
    }

    @GetMapping("/recent-action-logs")
    fun getRecentActionLog(
        @PathVariable projectId: Long,
        @AuthenticationPrincipal account: Account,
    ): ResponseEntity<Long> {
        return ResponseEntity
            .ok()
            .body(actionLogService.recentActionId(account, projectId))
    }

}