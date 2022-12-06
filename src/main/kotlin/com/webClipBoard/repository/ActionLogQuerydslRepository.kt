package com.webClipBoard.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.webClipBoard.ActionLog
import com.webClipBoard.Project
import com.webClipBoard.QActionLog.actionLog
import org.springframework.stereotype.Repository

@Repository
class ActionLogQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findMaxIdOrNull(projectId: Long): Long? {
        return queryFactory.select(actionLog.id.max())
            .from(actionLog)
            .where(actionLog.project.id.eq(projectId))
            .fetchOne()
    }

    fun findActionLogAfterLastId(project: Project, lastId: Long): List<ActionLog> {
        return queryFactory.selectFrom(actionLog)
            .where(
                actionLog.project.eq(project),
                actionLog.id.gt(lastId)
            )
            .orderBy(actionLog.id.asc())
            .fetch()
    }

}