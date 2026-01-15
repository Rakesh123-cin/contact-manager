package com.rakesh.Contact.Manager.Controller;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.Message;
import com.rakesh.Contact.Manager.Helpers.MessageType;
import com.rakesh.Contact.Manager.Services.ContactService;
import com.rakesh.Contact.Manager.Services.EmailService;
import com.rakesh.Contact.Manager.Services.ImageService;
import com.rakesh.Contact.Manager.Services.UserService;
import com.rakesh.Contact.Mnager.Forms.UserForm;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

@Controller
@RequestMapping("/user")    
public class UserController {

    private UserService userService;
    private ContactService contactService;
    private ImageService imageService;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, ContactService contactService, ImageService imageService, PasswordEncoder passwordEncoder, EmailService emailService)
    {
        this.userService = userService;
        this.contactService = contactService;
        this.imageService = imageService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // User profile page
    @RequestMapping(value="/profile", method = RequestMethod.GET)
    public String userProfile(Model model)
    {
        User loggedInUser = (User) model.getAttribute("loggedInUser");
        long totalContacts = contactService.getTotalContacts(loggedInUser);
        long favoriteContacts = contactService.getTotalFavoriteContacts(loggedInUser);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        return "user/profile";
    }

    // edit user profile page
    @RequestMapping(value="/edit-profile", method = RequestMethod.GET)
    public String editUserProfile(Model model)
    {
        User loggedInUser = (User) model.getAttribute("loggedInUser");
        UserForm userForm = new UserForm();
        userForm.setName(loggedInUser.getName());
        userForm.setEmail(loggedInUser.getEmail());
        userForm.setAbout(loggedInUser.getAbout());
        userForm.setPhoneNumber(loggedInUser.getPhoneNumber());
        userForm.setPassword(loggedInUser.getPassword());
        userForm.setProfilePic(loggedInUser.getProfilePic());
        model.addAttribute("userForm", userForm);
        return "user/update_userProfile";
    }

    // update user
    @RequestMapping(value="/update", method = RequestMethod.POST)
    public String updateUser(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult bindingResult, Model model, HttpSession session)
    {
        User loggedInUser = (User) model.getAttribute("loggedInUser");
        // validate contact form inputs
        if(bindingResult.hasErrors())
        {
            logger.info(bindingResult.toString());
            return "user/update_userProfile";
        }
        // contact image processing
        String imageUrl = loggedInUser.getProfilePic();
        if(userForm.getUserImage() != null && !userForm.getUserImage().isEmpty())
        {
            imageUrl = imageService.uploadImage(userForm.getUserImage());
        }
        // update user details
        loggedInUser.setName(userForm.getName());
        loggedInUser.setEmail(userForm.getEmail());
        loggedInUser.setAbout(userForm.getAbout());
        loggedInUser.setPhoneNumber(userForm.getPhoneNumber());
        loggedInUser.setPassword(passwordEncoder.encode(userForm.getPassword()));
        loggedInUser.setProfilePic(imageUrl);

        userService.updateUser(loggedInUser);

        // success message
        Message message = Message.builder().content("Contact Updated Successfully !!").type(MessageType.green).build();
        session.setAttribute("message", message);

        return "redirect:/user/edit-profile";
    }

    // get email page
    @RequestMapping(value="/mail", method = RequestMethod.GET)
    public String sendEmailPage()
    {
        return "user/mail";
    }

    // send email
    @RequestMapping(value="/send-mail", method = RequestMethod.POST)
    public String sendEmail( @RequestParam String to,
        @RequestParam String subject,
        @RequestParam String body,
        @RequestParam(required = false) MultipartFile attachment, Model model)
    {
        if(attachment != null && !attachment.isEmpty())
        {
            try {
                emailService.sendMail(to, subject, body, attachment);
            } catch (MessagingException ex) {
                model.addAttribute("errormessage", ex.getMessage());
                return "user/mail";
            }
        }
        else
        {
            emailService.sendMail(to, subject, body);
        }
        return "redirect:/user/mail";
    }
}
