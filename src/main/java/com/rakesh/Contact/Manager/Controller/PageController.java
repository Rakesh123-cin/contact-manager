package com.rakesh.Contact.Manager.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.EmailAlreadyExistsException;
import com.rakesh.Contact.Manager.Helpers.Message;
import com.rakesh.Contact.Manager.Helpers.MessageType;
import com.rakesh.Contact.Manager.Services.EmailService;
import com.rakesh.Contact.Manager.Services.ImageService;
import com.rakesh.Contact.Manager.Services.UserService;
import com.rakesh.Contact.Mnager.Forms.UserForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class PageController {

    private UserService userService;
    private ImageService imageService;
    private EmailService emailService;
    private Logger logger = LoggerFactory.getLogger(PageController.class);

    public PageController(UserService userService, ImageService imageService, EmailService emailService) {
        this.userService = userService;
        this.imageService = imageService;
        this.emailService = emailService;
    }

    @RequestMapping(value = {"/","/home"}, method = RequestMethod.GET) 
    public String home(Model model) {
        if(model.getAttribute("loggedInUser") != null)
        {
            return "redirect:/user/profile";
        }
        return "home";
    }

    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about()
    {
        System.out.println("About page loading....");
        return "about";
    }

    @RequestMapping(value = "/services", method = RequestMethod.GET)
    public String services()
    {
        System.out.println("Service page loading....");
        return "services";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @PostMapping("/sendFeedback")
    public String sendFeedback(@RequestParam String name,
                               @RequestParam String email,
                               @RequestParam String message) 
    {
        String to = "rakeshanandnewyou@gmail.com";
        String subject = "Feedback from " + name + " (" + email + ")";
        emailService.sendMail(to, subject, message);
        return "redirect:/contact";
    }   

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);

        return "register";
    }
    
    // Process registeration form
    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, Model model, HttpSession session)
    {
        logger.info("Processing registeration form...");
        //validate form data
        if(bindingResult.hasErrors())
        {
            logger.info("Error: " + bindingResult.toString());
            return "register";
        }
        // user image processing
        String profilePicUrl = "https://www.dreamstime.com/mobile-image321756561";
        if(userForm.getUserImage() != null && !userForm.getUserImage().isEmpty())
        {
            profilePicUrl = imageService.uploadImage(userForm.getUserImage());
        }
        //Create User from UserForm
        User user = User.builder()
        .name(userForm.getName())
        .email(userForm.getEmail())
        .password(userForm.getPassword())
        .about(userForm.getAbout())
        .phoneNumber(userForm.getPhoneNumber())
        .enabled(false)
        .emailVerified(false)
        .profilePic(profilePicUrl)
        .build();

        //save user to database
        try{
            User savedUser = userService.saveUser(user);
            System.out.println("Saved User: " + savedUser);
        }catch(EmailAlreadyExistsException ex)
        {
            System.out.println("Email already exists");
            model.addAttribute("errormessage", ex.getMessage());
            return "register";
        }
        
        // message success
        Message message = Message.builder().content("Successfully Registered !!").type(MessageType.green).build();
        session.setAttribute("message", message);

        //Redirect to register page
        return "redirect:/register";
    }

}
