package com.searchmovie.domain.user.repository;

import com.searchmovie.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
