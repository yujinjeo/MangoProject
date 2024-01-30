package com.demogroup.demoweb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.support.RedisRepositoryFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
내가 이해한 바로는, Redis도, 머신러닝 서버와 마찬가지로 http 통신으로 접근하는 DBMS이다.
내 컴퓨터에 Redis를 설치했다면 해당 서버가 실행되고,
이 스프링 프로젝트에서 Redis client 디펜던시를 추가 후
연결해서 사용한다.

 */
@Configuration
public class RedisConfig {

    /* 내 컴퓨터에 설치한 redis 서버에 접속하기 위해서
    * 컴퓨터의 host 문자열과
    * 서버 포트 (redis 서버 포트는 6379로 설정해두었다.)
    * */
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    /*
    * LettuceConnection을 생성하여 redis server와 연결하고,
    * redis cluster의 경우를 탐지하고 통합하여 동작할 수 있도록 조절하거나,
    * redis server 연결 파이프라인 답변에 대한 설정 등을 수행하는 클래스이다.
    * 쉽게 말해, redis server와 connection을 맺기 위해 필요한 객체이다.
    *
    * */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory(host,port);
    }

    /*
    * redis NoSQL DBMS를 스프링에서 사용하는 방법으로,
    * JDBC template와 비슷한 RedisTemplate(그런데 opsForValue()등의 함수가 마련되어 있어서 복잡하지 않다. NoSQL이라 쿼리 작성도 없음)
    * JPARepository ORM과 비슷한 RedisRepository가 있다.
    * RedisRepository의 경우, 내부에 CRUDRepository를 상속받은 레파지토리 인터페이스를 하나 생성하고,
    * key 값으로 사용할 dto 객체에 @RedisHash 어노테이션을 붙이면, 자동으로 해당 dto 객체가 key로 생성된다.
    * RedisRepository는 redis 모듈 내부적으로 CRUDRepository를 상속받고 있고, @EnableRedisRepository도 자동으로 활성화되어 있다.
    * 따라서 JPA처럼 save 함수라던지, deleteById 라던지 같은 함수를 사용할 수 있다.
    *
    * 나는 여기서 RedisTemplate를 사용하려고 한다.
    * */
    @Bean
    public RedisTemplate<String,String> redisTemplate(){
        RedisTemplate<String,String> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }




}
