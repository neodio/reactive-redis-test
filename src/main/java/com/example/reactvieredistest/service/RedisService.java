package com.example.reactvieredistest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisOperations<String, String> reactiveRedisOperations;
    private final ReactiveRedisConnectionFactory factory;
    private static final AtomicInteger count = new AtomicInteger(0);

    public void loadData() {
        /*List<String> data = new ArrayList<>();
        IntStream.range(0, 100000).forEach(i -> data.add(UUID.randomUUID().toString()));
        Flux<String> stringFlux = Flux.fromIterable(data);
        factory.getReactiveConnection().serverCommands()
                .flushAll()
                .thenMany(stringFlux.flatMap(uid -> reactiveRedisOperations.opsForValue().set(String.valueOf(count.getAndAdd(1)), uid)))
                .subscribe();*/

        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 50000; i++) {
            String key = "key_" + i;
            String value = "value_" + i;

            reactiveRedisOperations.opsForValue().set(key, value)
                    .subscribe(success -> log.info("redis set success."));
        }
        stopWatch.stop();
        System.out.println("시간=" + stopWatch.getTotalTimeSeconds());

    }

    public Flux<String> getNormalRedisDataList() {
        return Flux.fromIterable(Objects.requireNonNull(redisTemplate.keys("*")).stream()
                .map(key -> redisTemplate.opsForValue().get(key))
                .collect(Collectors.toList()));
    }

    public Flux<String> getReactiveRedisDataList() {
        return reactiveRedisOperations.keys("*").flatMap(key -> reactiveRedisOperations.opsForValue().get(key));
    }

    public Mono<String> getValue(String key) {
        return reactiveRedisOperations.opsForValue().get(key);
    }
}
