package com.example.article.service.request;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ArticleUpdateRequest {
    private String title;
    private String content;

    public ArticleUpdateRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
