// FILE: src/main/java/com/workshop/repository/UserRepository.java
package com.workshop.repository;

import com.workshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRollNumberAndRole(String rollNumber, String role);
    List<User> findAllByRoleOrderByFullNameAsc(String role);
    long countByRole(String role);
}
