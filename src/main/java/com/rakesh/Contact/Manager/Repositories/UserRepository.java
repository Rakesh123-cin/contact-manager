package com.rakesh.Contact.Manager.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rakesh.Contact.Manager.Entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    // custom finder methods
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailVerificationToken(String token);
}
