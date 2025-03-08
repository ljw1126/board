package com.example.comment.api;

import com.example.comment.service.response.CommentPageResponseV2;
import com.example.comment.service.response.CommentResponseV2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

class CommentApiV2Test {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponseV2 response1 = create(new CommentCreateRequestV2(1L, "my content1", null, 1L));
        CommentResponseV2 response2 = create(new CommentCreateRequestV2(1L, "my content2", response1.getPath(), 1L));
        CommentResponseV2 response3 = create(new CommentCreateRequestV2(1L, "my content3", response2.getPath(), 1L));

        System.out.println("response1.getPath(): " + response1.getPath());
        System.out.println("\tresponse2.getPath(): " + response2.getPath());
        System.out.println("\tresponse3.getPath(): " + response3.getPath());

        /**
         * response1.getPath(): 00000            // 155930799622705152
         * 	response2.getPath(): 0000000000      // 155930800369291264
         * 	response3.getPath(): 000000000000000 // 155930800419622912
         */
    }

    @Test
    void read() {
        CommentResponseV2 response = restClient.get()
                .uri("/v2/comments/{commentId}", 155930799622705152L)
                .retrieve()
                .body(CommentResponseV2.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v2/comments/{commentId}", 155930799622705152L)
                .retrieve();
    }

    CommentResponseV2 create(CommentCreateRequestV2 request) {
        return restClient.post()
                .uri("/v2/comments")
                .body(request)
                .retrieve()
                .body(CommentResponseV2.class);
    }

    @Test
    void readAll() {
        CommentPageResponseV2 response = restClient.get()
                .uri("/v2/comments?articleId=1&pageSize=10&page=50000")
                .retrieve()
                .body(CommentPageResponseV2.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for(com.example.comment.service.response.CommentResponseV2 comment : response.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

    }

    @Test
    void readAllInfiniteScroll() {
        List<com.example.comment.service.response.CommentResponseV2> response = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<com.example.comment.service.response.CommentResponseV2>>() {
                });

        System.out.println("firstPage");
        for(com.example.comment.service.response.CommentResponseV2 res : response) {
            System.out.println("comment.getCommentId() = " + res.getCommentId());
        }

        String lastPath = response.getLast().getPath();
        List<com.example.comment.service.response.CommentResponseV2> response2 = restClient.get()
                .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=" + lastPath)
                .retrieve()
                .body(new ParameterizedTypeReference<List<com.example.comment.service.response.CommentResponseV2>>() {
                });

        System.out.println("secondPage");
        for(com.example.comment.service.response.CommentResponseV2 res : response2) {
            System.out.println("comment.getCommentId() = " + res.getCommentId());
        }

    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequestV2 {
        private Long articleId;
        private String content;
        private String parentPath;
        private Long writerId;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    public static class CommentResponseV2 {
        private Long commentId;
        private String content;
        private Long articleId;
        private Long writerId;
        private Boolean deleted;
        private String path;
        private LocalDateTime createdAt;
    }

    @Test
    void countTest() {
        CommentResponseV2 response = create(new CommentCreateRequestV2(2L, "my content1", null, 1L));

        Long count = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count = " + count); // 1

        restClient.delete()
                .uri("/v2/comments/{commentId}", response.getCommentId())
                .retrieve();

        Long count2 = restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", 2L)
                .retrieve()
                .body(Long.class);
        System.out.println("count2 = " + count2); // 0
    }
}
