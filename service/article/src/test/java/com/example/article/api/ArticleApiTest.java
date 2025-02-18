package com.example.article.api;

import com.example.article.service.request.ArticleCreateRequest;
import com.example.article.service.request.ArticleUpdateRequest;
import com.example.article.service.response.ArticlePageResponse;
import com.example.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void create() {
        ArticleResponse response = restClient.post()
                        .uri("/v1/article")
                        .body(new ArticleCreateRequest("hi", "my content", 1L, 1L))
                        .retrieve()
                        .body(ArticleResponse.class);

        System.out.println(response);
    }

    @Test
    void read() {
        ArticleResponse response = restClient.get()
                .uri("/v1/article/{articleId}", 149866417983766528L)
                .retrieve()
                .body(ArticleResponse.class);

        System.out.println(response);
    }

    @Test
    void readAll() {
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
                .retrieve()
                .body(ArticlePageResponse.class);

        System.out.println("response.getCount() = " + response.getCount());
        for(ArticleResponse article : response.getArticles()) {
            System.out.println(article.getArticleId());
        }
    }

    @Test
    void update() {
        ArticleResponse response = restClient.put()
                .uri("/v1/article/{articleId}", 149866417983766528L)
                .body(new ArticleUpdateRequest("hi 2", "my content 2"))
                .retrieve()
                .body(ArticleResponse.class);

        System.out.println(response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/article/{articleId}", 149866417983766528L)
                .retrieve();
    }
}
