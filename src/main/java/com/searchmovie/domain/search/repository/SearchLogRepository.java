package com.searchmovie.domain.search.repository;

import com.searchmovie.domain.search.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long>, SearchLogRepositoryCustom {
}
