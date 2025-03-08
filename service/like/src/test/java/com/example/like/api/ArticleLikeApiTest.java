package com.example.like.api;

import com.example.like.service.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ArticleLikeApiTest {
    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void test() {
        Long articleId = 9999L;

        like(articleId, 1L);
        like(articleId, 2L);
        like(articleId, 3L);

        System.out.println(read(articleId, 1L));
        System.out.println(read(articleId, 2L));
        System.out.println(read(articleId, 3L));

        unlike(articleId, 1L);
        unlike(articleId, 2L);
        unlike(articleId, 3L);
    }

    ArticleLikeResponse read(Long articleId, Long userId) {
        return restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve()
                .body(ArticleLikeResponse.class);
    }

    void like(Long articleId, Long userId) {
        restClient.post()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();
    }

    void likeWithLock(Long articleId, Long userId, String lockType) {
        restClient.post()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}/" + lockType, articleId, userId)
                .retrieve();
    }

    void unlike(Long articleId, Long userId) {
        restClient.delete()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
                .retrieve();
    }


    @Test
    void likePerformanceTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        likePerformanceTest(executorService, 1111L, "pessimistic-lock-1");
        likePerformanceTest(executorService, 2222L, "pessimistic-lock-2");
        likePerformanceTest(executorService, 3333L, "optimistic-lock");
    }

    private void likePerformanceTest(ExecutorService executorService, Long articleId, String lockType) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3000);
        System.out.println(lockType + " start");

        likeWithLock(articleId, 1L, lockType);

        long start = System.nanoTime();
        for(int i = 0; i < 3000; i++) {
            long userId = i + 2;
            executorService.submit(() -> {
                likeWithLock(articleId, userId, lockType);
                latch.countDown();
            });
        }

        latch.await();

        long end = System.nanoTime();

        System.out.println("lockType = " + lockType + ", time = " + (end - start) / 1000000 + "ms");
        System.out.println(lockType + " end");
        System.out.println("count = " + count(articleId));
    }

    Long count(Long articleId) {
        return restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long.class);
    }
}
