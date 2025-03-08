package com.example.view.service;

import com.example.view.entity.ArticleViewCount;
import com.example.view.repository.ArticleViewCountBackUpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ArticleViewCountBackUpProcessor {

    private final ArticleViewCountBackUpRepository articleViewCountBackUpRepository;

    @Transactional
    public void backUp(Long articleId, Long viewCount) {
        int result= articleViewCountBackUpRepository.updateViewCount(articleId, viewCount);
        if(result == 0) {
            articleViewCountBackUpRepository.findById(articleId)
                    .ifPresentOrElse(
                            ignored -> {},
                            () -> articleViewCountBackUpRepository.save(ArticleViewCount.of(articleId, viewCount))
                    );
        }
    }

}
