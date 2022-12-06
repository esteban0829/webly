package com.webClipBoard.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import com.webClipBoard.Project
import com.webClipBoard.QProject.project
import com.webClipBoard.QProjectAccount.projectAccount
import org.springframework.stereotype.Repository

@Repository
class ProjectQuerydslRepository(
    private val queryFactory: JPAQueryFactory,
) {

    fun findByAccountId(accountId: Long): List<Project> {
        return queryFactory.select(project)
            .from(projectAccount)
            .join(projectAccount.project, project)
            .where(projectAccount.account.id.eq(accountId))
            .fetch()
    }

}