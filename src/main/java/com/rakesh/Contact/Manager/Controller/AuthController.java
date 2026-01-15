package com.rakesh.Contact.Manager.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.Message;
import com.rakesh.Contact.Manager.Helpers.MessageType;
import com.rakesh.Contact.Manager.Services.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token, HttpSession session)
    {
        User user = userService.getUserByEmailVerificationToken(token);
        if(user!=null)
        {
            user.setEnabled(true);
            user.setEmailVerified(true);
            userService.updateUser(user);
            return "redirect:/login";
        }
        Message message = Message.builder()
                .content("Email not verified!! Something went wrong....")
                .type(MessageType.red)
                .build();
        session.setAttribute("message", message);
        return "error_page";
    }
}
