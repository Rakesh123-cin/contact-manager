package com.rakesh.Contact.Manager;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.AppConstants;
import com.rakesh.Contact.Manager.Repositories.UserRepository;

@SpringBootApplication
public class ContactManagerApplication implements CommandLineRunner{

	private PasswordEncoder passwordEncoder;
	private UserRepository userRepository;
	private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactManagerApplication.class);

	public ContactManagerApplication(PasswordEncoder passwordEncoder, UserRepository userRepository) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(ContactManagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setName("Test");
		user.setEmail("test@gmail.com");
		user.setPassword(passwordEncoder.encode("Test@123"));
		user.setRoles(List.of(AppConstants.ROLE));
		user.setEnabled(true);
		user.setEmailVerified(true);
		user.setAbout("This is a dummy user");
		user.setPhoneNumberVerified(true);

		userRepository.findByEmail("test@gmail.com").ifPresentOrElse(
			(u)->{
				logger.info("User already exists");
			},
			()->{
				userRepository.save(user);
				logger.info("Dummy user created");
			}
		);

	}
}
