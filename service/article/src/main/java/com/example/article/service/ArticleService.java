package com.example.article.service;

import com.example.article.entity.Article;
import com.example.article.entity.BoardArticleCount;
import com.example.article.repository.ArticleRepository;
import com.example.article.repository.BoardArticleCountRepository;
import com.example.article.service.request.ArticleCreateRequest;
import com.example.article.service.request.ArticleUpdateRequest;
import com.example.article.service.response.ArticlePageResponse;
import com.example.article.service.response.ArticleResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final Snowflake snowflake = new Snowflake();

    private final ArticleRepository articleRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Transactional
    public ArticleResponse create(ArticleCreateRequest request) {
        Article article = articleRepository.save(Article.create(snowflake.nextId(), request.getTitle(), request.getContent(), request.getBoardId(), request.getWriterId()));
        int result = boardArticleCountRepository.increase(request.getBoardId());
        if(result == 0) {
            boardArticleCountRepository.save(BoardArticleCount.of(request.getBoardId(), 1L));
        }

        return ArticleResponse.from(article);
    }

    @Transactional
    public ArticleResponse update(Long articleId, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.update(request.getTitle(), request.getContent());
        return ArticleResponse.from(article);
    }

    @Transactional(readOnly = true)
    public ArticleResponse read(Long articleId) {
        return ArticleResponse.from(articleRepository.findById(articleId).orElseThrow());
    }

    @Transactional
    public void delete(Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        articleRepository.delete(article);
        boardArticleCountRepository.decrease(article.getBoardId());
    }

    @Transactional(readOnly = true)
    public ArticlePageResponse readAll(Long boardId, Long page, Long pageSize) {
        return new ArticlePageResponse(
                articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize)
                        .stream()
                        .map(ArticleResponse::from)
                        .toList(),
                articleRepository.count(
                        boardId,
                        PageLimitCalculator.pageLimit(page, pageSize, 10L)
                )
        );
    }

    @Transactional(readOnly = true)
    public List<ArticleResponse> readAllInfiniteScroll(Long boardId, Long pageSize, Long lastArticleId) {
        List<Article> articles = lastArticleId == null ?
                articleRepository.findAllInfiniteScroll(boardId, pageSize) :
                articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId);

        return articles.stream().map(ArticleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Long count(Long boardId) {
        return boardArticleCountRepository.findById(boardId)
                .map(BoardArticleCount::getArticleCount)
                .orElse(0L);
    }
}
