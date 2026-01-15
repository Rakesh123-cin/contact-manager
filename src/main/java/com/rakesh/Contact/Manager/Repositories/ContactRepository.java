package com.rakesh.Contact.Manager.Repositories;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.rakesh.Contact.Manager.Entities.Contact;
import com.rakesh.Contact.Manager.Entities.User;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    boolean existsByUserAndPhoneNumber(User user,String phoneNumber);

    Page<Contact> findByUser(User user, Pageable pageable);

    @Query("select s from Contact s where s.user.userId = ?1")
    List<Contact> findByUserId(String userId);

    Page<Contact> findByUserAndNameContaining(User user, String nameKeyword, Pageable pageable);

    Page<Contact> findByUserAndEmailContaining(User user, String emailKeyword, Pageable pageable);

    Page<Contact> findByUserAndPhoneNumberContaining(User user, String phoneNumberKeyword, Pageable pageable);

    // Total contacts count for a user
    long countByUser(User user);

    // Count favorite contacts for a user
    long countByUserAndFavoriteTrue(User user);

}