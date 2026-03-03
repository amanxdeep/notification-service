package com.self.notificationService.repository;

import com.self.notificationService.model.entity.ConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {
    Optional<ConfigEntity> findByGroupAndKey(String group, String key);
}
