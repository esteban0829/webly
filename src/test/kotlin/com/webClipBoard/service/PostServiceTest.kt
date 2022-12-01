package com.webClipBoard.service

import com.webClipBoard.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    lateinit var postService: PostService
    @Autowired
    lateinit var postRepository: PostRepository
    @Autowired
    lateinit var accountRepository: AccountRepository
    @Autowired
    lateinit var fileRepository: FileRepository

    @Test
    fun getPosts() {
        // given
        val account = accountRepository.save(Account.fixture())
        val file = fileRepository.save(File.fixture())
        val post = postRepository.save(Post.fixture(
            creator = account,
            content = "content",
            title = "title",
            file = file,
        ))

        // when
        val result = postService.getPosts(account)

        // then
        assertThat(result).hasSize(1)
        result[0].run {
            assertThat(id).isEqualTo(post.id)
            assertThat(content).isEqualTo("content")
            assertThat(title).isEqualTo("title")
            assertThat(fileId).isEqualTo(file.id)
        }
    }

    @Test
    fun createPost() {
        // given
        val account = accountRepository.save(Account.fixture())
        val file = fileRepository.save(File.fixture())
        val request = CreatePostDTO.fixture(
            title = "title",
            content = "content",
            fileId = file.id,
        )

        // when
        val result = postService.createPost(account, request)

        // then
        val posts = postRepository.findAll()
        assertThat(posts).hasSize(1)
        posts[0].run {
            assertThat(result).isEqualTo(id)
            assertThat(title).isEqualTo("title")
            assertThat(content).isEqualTo("content")
            assertThat(this.file).isEqualTo(file)
        }
    }

    @Test
    fun deletePost() {
        // given
        val account = accountRepository.save(Account.fixture())
        val file = fileRepository.save(File.fixture())
        val post = postRepository.save(Post.fixture(
            creator = account,
            file = file,
        ))

        // when
        postService.deletePost(account, post.id!!)

        // then
        val result = postRepository.findAll()
        assertThat(result).isEmpty()
    }
}