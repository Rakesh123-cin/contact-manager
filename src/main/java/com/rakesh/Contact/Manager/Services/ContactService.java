package com.rakesh.Contact.Manager.Services;

import com.rakesh.Contact.Manager.Entities.Contact;
import com.rakesh.Contact.Manager.Entities.User;

import java.util.List;

import org.springframework.data.domain.Page;

public interface ContactService {

    // save contact
    Contact save(Contact contact);

    // update contact
    Contact update(Contact contact);

    // get contacts
    List<Contact> getAll();

    // get contact by id
    Contact getById(String id);

    // delete contact
    void delete(String id);

    // search contact
    Page<Contact> searchByName(String name, int pageNO, int pageSize, String sortBy, String sortDirection, User user);

    Page<Contact> searchByEmail(String email, int pageNO, int pageSize, String sortBy, String sortDirection, User user);

    Page<Contact> searchByPhoneNumber(String phoneNumber, int pageNo, int pageSize, String sortBy, String sortDirection, User user);

    // get contacts by userId
    List<Contact> getByUserId(String userId);

    // get contacts by user
    Page<Contact> getByUser(User user, int pageNo, int pageSize, String sortBy, String sortDirection);

    // get total contacts count for a user
    long getTotalContacts(User user);

    // get total favorite contacts count for a user
    long getTotalFavoriteContacts(User user);

}
