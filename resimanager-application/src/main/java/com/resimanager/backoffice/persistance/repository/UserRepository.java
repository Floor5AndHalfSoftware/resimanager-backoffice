package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
