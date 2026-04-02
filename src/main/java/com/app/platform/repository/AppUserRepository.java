package com.app.platform.repository;

import com.app.platform.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	Optional<AppUser> findByLoginName(String loginName);

	boolean existsByLoginName(String loginName);
}
