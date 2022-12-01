package com.webClipBoard.service

import com.webClipBoard.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(
    private val postRepository: PostRepository,
    private val fileRepository: FileRepository,
) {

    @Transactional(readOnly = true)
    fun getPosts(account: Account): List<PostDTO> {
        return postRepository.findByCreatorOrderByUpdateDateTime(account).map { PostDTO.of(it) }
    }

    @Transactional
    fun createPost(account: Account, createPostDTO: CreatePostDTO): Long {
        val file: File? = createPostDTO.fileId?.let {
            fileRepository.findById(it).getElseThrowNotFoundException(it)
        }
        return postRepository.save(createPostDTO.run {
            Post(
                title = title,
                creator = account,
                file = file,
                content = content,
            )
        }).id!!
    }

    @Transactional
    fun deletePost(account: Account, id: Long) {
        postRepository.findById(id)
            .getElseThrowNotFoundException(id)
            .also {
                if (it.creator != account)
                    throw UnAuthorizedPostException()
            }
            .run {
                postRepository.delete(this)
            }
    }
}