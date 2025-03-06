package com.example.comment.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

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
}
