package com.example.article.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PageLimitCalculatorTest {

    @Test
    void pageLimit() {
        calcualtePageLimitTest(1L, 30L, 10L, 301L);
        calcualtePageLimitTest(7L, 30L, 10L, 301L);
        calcualtePageLimitTest(10L, 30L, 10L, 301L);
        calcualtePageLimitTest(11L, 30L, 10L, 601L);
        calcualtePageLimitTest(20L, 30L, 10L, 601L);
        calcualtePageLimitTest(21L, 30L, 10L, 901L);
    }

    void calcualtePageLimitTest(Long page, Long pageSize, Long movablePageCount, Long expected) {
        Long result = PageLimitCalculator.pageLimit(page, pageSize, movablePageCount);
        assertThat(result).isEqualTo(expected);
    }
}
