package com.example.article;

import com.example.article.service.request.ArticleCreateRequest;
import com.example.article.service.request.ArticleUpdateRequest;
import com.example.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

@Disabled
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
