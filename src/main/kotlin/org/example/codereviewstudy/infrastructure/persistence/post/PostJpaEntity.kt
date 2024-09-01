package org.example.codereviewstudy.infrastructure.persistence.post

import jakarta.persistence.*
import org.example.codereviewstudy.infrastructure.persistence.model.BaseTimeEntity
import org.example.codereviewstudy.domain.post.model.Post
import org.example.codereviewstudy.infrastructure.persistence.user.UserJpaEntity
import org.example.codereviewstudy.infrastructure.persistence.user.toUser
import org.hibernate.annotations.Comment

@Entity
@Table(name = "posts", indexes = [Index(name = "idx_post_title", columnList = "title")])
class PostJpaEntity(
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    var author: UserJpaEntity,

    @Column(name = "title", nullable = false, length = 255)
    @Comment("제목")
    var title: String,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @Comment("본문")
    var content: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
) : BaseTimeEntity()

fun PostJpaEntity.toDomain(): Post {
    return Post(
        author = author.toUser(),
        title = title,
        content = content,
        createdAt = createdAt,
        id = id,
    )
}