package com.rakesh.Contact.Manager.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.Helper;
import com.rakesh.Contact.Manager.Services.UserService;

@ControllerAdvice
public class RootController {

    private UserService userService;
    Logger logger = LoggerFactory.getLogger(RootController.class);

    public RootController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication)
    {
        if(authentication==null || !authentication.isAuthenticated())
            return;
        
        String email = Helper.getEmailOfLoggedInUser(authentication);
        logger.info("User logged in : {} ",email);
        User user = userService.getUserByEmail(email);
        model.addAttribute("loggedInUser", user);
    }

}
