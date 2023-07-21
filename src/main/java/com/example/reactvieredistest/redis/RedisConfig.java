package com.example.reactvieredistest.redis;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@AllArgsConstructor
public class RedisConfig {

    private final RedisProperties redisProperties;

    @Bean
    public ClusterTopologyRefreshOptions clusterTopologyRefreshOptions() {
        return ClusterTopologyRefreshOptions.builder()
                .enablePeriodicRefresh(Duration.ofSeconds(30))
                .enableAllAdaptiveRefreshTriggers()
                .build();
    }

    @Bean
    public ClusterClientOptions clusterClientOptions() {
        return ClusterClientOptions.builder()
                .topologyRefreshOptions(clusterTopologyRefreshOptions())
                .build();
    }

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(redisProperties.getRedisNodes());

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration
                .builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .clientOptions(clusterClientOptions())
                .commandTimeout(Duration.ofMillis(redisProperties.getCommands().getTimeoutMilliSeconds()))
                .build();

        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    @Primary
    @Bean
    ReactiveRedisOperations<String, String> redisOperations() {
        RedisSerializer<String> serializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> serializationContext = RedisSerializationContext.
                <String, String>newSerializationContext().key(serializer)
                .value(serializer).hashKey(serializer)
                .hashValue(serializer)
                .build();
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory(), serializationContext);
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        redisClusterConfiguration.setClusterNodes(redisProperties.getRedisNodes());

        LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration
                .builder()
                .readFrom(ReadFrom.REPLICA_PREFERRED)
                .clientOptions(clusterClientOptions())
                .commandTimeout(Duration.ofMillis(redisProperties.getCommands().getTimeoutMilliSeconds()))
                .build();

        return new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory());

        return redisTemplate;
    }
}
