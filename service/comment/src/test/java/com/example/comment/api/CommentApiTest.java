package com.example.comment.api;

import com.example.comment.service.response.CommentPageResponse;
import com.example.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = create(new CommentCreateRequest(1L, "my comment1", null, 1L));
        CommentResponse response2 = create(new CommentCreateRequest(1L, "my comment2", response1.getCommentId(), 1L));
        CommentResponse response3 = create(new CommentCreateRequest(1L, "my comment3", response1.getCommentId(), 1L));

        System.out.println("commentId = %s".formatted(response1.getCommentId()));
        System.out.println("commentId = %s".formatted(response2.getCommentId()));
        System.out.println("commentId = %s".formatted(response3.getCommentId()));

        /*commentId = 155659926230863872
        commentId = 155659926809677824
        commentId = 155659926851620864*/
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 155659926230863872L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 155659926851620864L)
                .retrieve();
    }


    CommentResponse create(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId=1&page=1&pageSize=10")
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        List<CommentResponse> comments = response.getComments();
        for(CommentResponse comment : comments) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }

            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> response = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        System.out.println("firstPage");
        for(CommentResponse comment : response) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }

            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        Long lastParentCommentId = response.getLast().getParentCommentId();
        Long lastCommentId = response.getLast().getCommentId();

        List<CommentResponse> response2 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s".formatted(
                        lastParentCommentId, lastCommentId
                ))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        System.out.println("secondPage");
        for(CommentResponse comment : response2) {
            if(!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }

            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

    }


}
