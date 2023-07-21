package com.example.reactvieredistest.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisNode;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@ConfigurationProperties(prefix = "redis.cluster")
@Setter
@Getter
public class RedisProperties {
    private String clusterHosts;
    private Set<RedisNode> redisNodes;
    private Commands commands;

    public Set<RedisNode> getRedisNodes() {
        redisNodes = Arrays.stream(clusterHosts.split(",")).map(host -> {
            String[] hostAndPort = host.split(":");
            return new RedisNode(hostAndPort[0], Short.parseShort(hostAndPort[1]));
        }).collect(Collectors.toSet());

        return redisNodes;
    }

    @Getter
    @Setter
    @ToString
    public static class Commands {
        private long timeoutMilliSeconds;
        private long asyncTimeoutMilliSeconds;
        private long setBaseExpireSeconds;
    }
}
