package com.easyplan._04_infra.dsl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Configuration
public class QueryDSLConfig {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Bean
	JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager); 
	}
}
