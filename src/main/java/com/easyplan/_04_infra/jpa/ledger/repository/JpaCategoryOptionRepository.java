package com.easyplan._04_infra.jpa.ledger.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.easyplan._04_infra.jpa.ledger.entity.CategoryOptionEntity;

public interface JpaCategoryOptionRepository extends JpaRepository<CategoryOptionEntity, Long> {
	CategoryOptionEntity findByOptionCode(String optionCode);
	
	List<CategoryOptionEntity> findAllByOptionCodeIn(Set<String> optionCode);
}
