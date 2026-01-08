package com.searchmovie.domain.search.repository;

import com.searchmovie.domain.search.entity.HotKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotKeywordRepository extends JpaRepository<HotKeyword, Long>, HotKeywordRepositoryCustom {
}
