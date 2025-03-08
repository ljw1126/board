package com.example.article.api;

import com.example.article.service.request.ArticleCreateRequest;
import com.example.article.service.request.ArticleUpdateRequest;
import com.example.article.service.response.ArticlePageResponse;
import com.example.article.service.response.ArticleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class ArticleApiTest {
    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void create() {
        ArticleResponse response = createArticle(new ArticleCreateRequest("hi", "my content", 1L, 1L));

        System.out.println(response);
    }

    private ArticleResponse createArticle(ArticleCreateRequest body) {
        ArticleResponse response = restClient.post()
                        .uri("/v1/article")
                        .body(body)
                        .retrieve()
                        .body(ArticleResponse.class);
        return response;
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
    void readAllInfiniteScroll() {
        List<ArticleResponse> articles = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("firstPage");
        for(ArticleResponse article : articles) {
            System.out.println(article.getArticleId());
        }

        Long lastArticleId = articles.getLast().getArticleId();
        List<ArticleResponse> articles2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=" + lastArticleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });

        System.out.println("secondPage");
        for(ArticleResponse article : articles2) {
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

    @Test
    void countTest() {
        ArticleResponse response = createArticle(new ArticleCreateRequest("hi", "content", 1L, 2L));

        Long count = restClient.get()
                .uri("/v1/article/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);

        System.out.println("count = " + count);

        restClient.delete()
                .uri("/v1/article/{articleId}", response.getArticleId())
                .retrieve();


        Long count2 = restClient.get()
                .uri("/v1/article/boards/{boardId}/count", 2L)
                .retrieve()
                .body(Long.class);

        System.out.println("count2 = " + count2);
    }
}
