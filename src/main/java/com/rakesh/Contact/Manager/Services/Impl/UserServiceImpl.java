package com.rakesh.Contact.Manager.Services.Impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.AppConstants;
import com.rakesh.Contact.Manager.Helpers.EmailAlreadyExistsException;
import com.rakesh.Contact.Manager.Helpers.Helper;
import com.rakesh.Contact.Manager.Helpers.ResourceNotFoundException;
import com.rakesh.Contact.Manager.Repositories.UserRepository;
import com.rakesh.Contact.Manager.Services.EmailService;
import com.rakesh.Contact.Manager.Services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public User saveUser(User user) {
        if(userRepository.existsByEmail(user.getEmail()))
            throw new EmailAlreadyExistsException("Email alraedy exists");
        // Dynamically generate userId
        String userId = java.util.UUID.randomUUID().toString();
        user.setUserId(userId);

        // encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // set default role
        user.getRoles().add(AppConstants.ROLE);

        // set email verification token
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        User savedUser = userRepository.save(user);
        String emailVerificationLink = Helper.getEmailVerificationLink(token);
        emailService.sendMail(savedUser.getEmail(), "Verify account: Smart Contact Manager", emailVerificationLink);

        return savedUser;
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User updatedUser = userRepository.save(user);
        return Optional.ofNullable(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("User not found in the database");
        }   );

        userRepository.delete(user);
    }

    @Override
    public boolean isUserExist(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
       return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User with email "+email+" not found"));
    }

    @Override
    public User getUserByEmailVerificationToken(String token) {
        return userRepository.findByEmailVerificationToken(token).orElse(null);
    }

}
