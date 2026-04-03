package com.app.platform.sm.menu.repository;

import com.app.platform.sm.menu.domain.AppMenu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppMenuRepository extends JpaRepository<AppMenu, Long> {
}
