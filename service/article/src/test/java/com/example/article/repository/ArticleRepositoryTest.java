package com.example.article.repository;

import com.example.article.entity.Article;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Test
    void findAll() {
        List<Article> articles = articleRepository.findAll(1L, 1499970L, 30L);
        log.info("article size = {}", articles.size());
        for(Article article : articles) {
            log.info("article = {}", article);
        }
    }

    @Test
    void count() {
        Long count = articleRepository.count(1L, 10000L);
        log.info("count = {}", count);
    }
}
