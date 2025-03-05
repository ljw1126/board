package com.example.article.service.response;

import com.example.article.entity.Article;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public class ArticleResponse {
    private Long articleId;
    private String title;
    private String content;
    private Long boardId;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ArticleResponse(Long articleId, String title, String content, Long boardId, Long writerId, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.articleId = articleId;
        this.title = title;
        this.content = content;
        this.boardId = boardId;
        this.writerId = writerId;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ArticleResponse from(Article article) {
        return new ArticleResponse(article.getArticleId(),
                article.getTitle(),
                article.getContent(),
                article.getBoardId(),
                article.getWriterId(),
                article.getCreatedAt(),
                article.getModifiedAt());
    }
}
