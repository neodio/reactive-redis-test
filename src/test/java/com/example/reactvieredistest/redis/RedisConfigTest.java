package com.example.reactvieredistest.redis;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@ActiveProfiles("local")
@SpringBootTest
class RedisConfigTest {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Test
    public void opsValue() {
        ReactiveValueOperations<String, String> valueOps = reactiveRedisTemplate.opsForValue();
        Set<String> cacheKeys = new HashSet<>();
        // async process
        log.info("Step-1");
        for (int i = 0; i < 5000; i++) {
            String key = "value_" + i;
            cacheKeys.add(key);
            valueOps.set(key, String.valueOf(i));
        }
        log.info("Step-2");
        Mono<List<String>> values = valueOps.multiGet(cacheKeys);
        log.info("Step-3");
        StepVerifier.create(values)
                .expectNextMatches(x -> x.size() == 5000).verifyComplete();
        log.info("Step-4");
    }
}