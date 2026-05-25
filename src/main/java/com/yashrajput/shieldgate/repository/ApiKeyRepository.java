package com.yashrajput.shieldgate.repository;

import com.yashrajput.shieldgate.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyValue(String keyValue);

    List<ApiKey> findByUserEmail(String email);

    long countByActive(boolean active);

    void deleteById(Long id);
}