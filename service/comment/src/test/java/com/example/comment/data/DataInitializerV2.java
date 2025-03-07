package com.example.comment.data;

import com.example.comment.entity.CommentPath;
import com.example.comment.entity.CommentV2;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import kuke.board.common.snowflake.Snowflake;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class DataInitializerV2 {

    private static final int BULK_INSERT_SIZE = 2000;
    private static final int EXECUTE_COUNT = 5000;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final Snowflake snowflake = new Snowflake();
    private final CountDownLatch latch = new CountDownLatch(EXECUTE_COUNT);

    // 멀티스레드에서 유니크한 값을 생성하기 위해 start, end 범위로 처리
    @Test
    void init() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for(int i = 0; i < EXECUTE_COUNT; i++) {
            int start = i * BULK_INSERT_SIZE;
            int end = (i + 1) * BULK_INSERT_SIZE;
            executorService.submit(() -> {
                insert(start, end);
                latch.countDown();
                System.out.println("latch.getCount() = " + latch.getCount());
            });
        }

        latch.await();
        executorService.shutdown();
    }

    private void insert(int start, int end) {
        transactionTemplate.executeWithoutResult(status -> {
            for(int i = start; i < end; i++) {
                CommentV2 comment = CommentV2.of(
                        snowflake.nextId(),
                        "content",
                        1L,
                        1L,
                        toPath(i)
                );

                entityManager.persist(comment);
            }
        });
    }

    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private static final int DEPTH_CHUNK_SIZE = 5;

    private CommentPath toPath(int value) {
        String result = "";
        int charsetLength = CHARSET.length();
        for(int i = 0; i < DEPTH_CHUNK_SIZE; i++) {
            result = CHARSET.charAt(value % charsetLength) + result;
            value /= charsetLength;
        }
        return CommentPath.create(result);
    }



}
