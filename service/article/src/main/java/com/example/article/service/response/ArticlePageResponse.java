package com.example.article.service.response;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ArticlePageResponse {
    private List<ArticleResponse> articles;
    private Long count;

    public ArticlePageResponse(List<ArticleResponse> articles, Long count) {
        this.articles = articles;
        this.count = count;
    }
}
