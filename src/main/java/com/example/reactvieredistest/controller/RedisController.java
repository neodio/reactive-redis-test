package com.example.reactvieredistest.controller;

import com.example.reactvieredistest.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RedisController {

    @Autowired
    private RedisService redisService;

    @GetMapping("/load-data-list")
    public void loadDataList() {
        redisService.loadData();
    }

    @GetMapping("/normal-redis-get-data-list")
    public Flux<String> normalRedisGetDataList() {
        return redisService.getNormalRedisDataList();
    }

    @GetMapping("/reactive-redis-get-data-list")
    public Flux<String> reactiveRedisGetDataList() {
        return redisService.getReactiveRedisDataList();
    }

    @GetMapping("/get-value/{key}")
    public Mono<String> getValue(@PathVariable String key) {
        return redisService.getValue(key);
    }
}
