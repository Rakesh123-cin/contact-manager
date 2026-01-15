package com.rakesh.Contact.Manager.Services.Impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.rakesh.Contact.Manager.Entities.Contact;
import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.PhoneNumberAlreadyExistsException;
import com.rakesh.Contact.Manager.Repositories.ContactRepository;
import com.rakesh.Contact.Manager.Services.ContactService;
import com.rakesh.Contact.Manager.Helpers.ResourceNotFoundException;

@Service
public class ContactServiceImpl implements ContactService{

    private ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository)
    {
        this.contactRepository = contactRepository;
    }

    @Override
    public Contact save(Contact contact) {
        if(contactRepository.existsByUserAndPhoneNumber(contact.getUser(), contact.getPhoneNumber()))
        {
            throw new PhoneNumberAlreadyExistsException(contact.getPhoneNumber()+" Phone number already exists");
        }
        String id = UUID.randomUUID().toString();
        contact.setId(id);
        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with id: "+id));
    }

    @Override
    public void delete(String id) {
        Contact contact = contactRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contact not found with id: "+id));
        contactRepository.delete(contact);
    }


    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepository.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int pageNo, int pageSize, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        return contactRepository.findByUser(user, pageable);
    }

    @Override
    public Page<Contact> searchByName(String name, int pageNo, int pageSize, String sortBy, String sortDirection,User user) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return contactRepository.findByUserAndNameContaining(user, name, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String email, int pageNo, int pageSize, String sortBy, String sortDirection, User user) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return contactRepository.findByUserAndEmailContaining(user, email, pageable);
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneNumber, int pageNo, int pageSize, String sortBy,  String sortDirection,User user) {
        Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return contactRepository.findByUserAndPhoneNumberContaining(user, phoneNumber, pageable);
    }

    @Override
    public long getTotalContacts(User user) {
        return contactRepository.countByUser(user);
    }

    @Override
    public long getTotalFavoriteContacts(User user) {
        return contactRepository.countByUserAndFavoriteTrue(user);
    }

}
