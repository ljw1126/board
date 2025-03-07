package com.example.comment.service;

import com.example.comment.entity.Comment;
import com.example.comment.repository.CommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @DisplayName("삭제할 댓글에 자식이 있으면, 삭제 표시만 한다")
    @Test
    void case1() {
        Long articleId = 1L;
        Long commentId = 2L;

        Comment comment = create(articleId, commentId);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
                .willReturn(2L);

        commentService.delete(commentId);

        verify(comment).delete();
    }

    @DisplayName("하위 댓글이 삭제되고, 삭제되지 않은 부모면, 하위 댓글만 삭제한다.")
    @Test
    void case2() {
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = create(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = mock(Comment.class);
        given(parentComment.getDeleted()).willReturn(false);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
                .willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parentComment));

        commentService.delete(commentId);

        verify(commentRepository).delete(comment);
        verify(commentRepository, never()).delete(parentComment);
    }

    @DisplayName("하위 댓글이 삭제되고, 삭제된 부모면, 재귀적으로 모두 삭제한다")
    @Test
    void case3() {
        Long articleId = 1L;
        Long commentId = 2L;
        Long parentCommentId = 1L;

        Comment comment = create(articleId, commentId, parentCommentId);
        given(comment.isRoot()).willReturn(false);

        Comment parentComment = create(articleId, parentCommentId);
        given(parentComment.isRoot()).willReturn(true);
        given(parentComment.getDeleted()).willReturn(true);

        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        given(commentRepository.countBy(articleId, commentId, 2L))
                .willReturn(1L);

        given(commentRepository.findById(parentCommentId))
                .willReturn(Optional.of(parentComment));
        given(commentRepository.countBy(articleId, parentCommentId, 2L))
                .willReturn(1L);

        commentService.delete(commentId);

        verify(commentRepository).delete(comment);
        verify(commentRepository).delete(parentComment);
    }

    private Comment create(Long articleId, Long commentId) {
        Comment comment = mock(Comment.class);
        given(comment.getArticleId()).willReturn(articleId);
        given(comment.getCommentId()).willReturn(commentId);
        return comment;
    }

    private Comment create(Long articleId, Long commentId, Long parentCommentId) {
        Comment comment = create(articleId, commentId);
        given(comment.getParentCommentId()).willReturn(parentCommentId);
        return comment;
    }
}
