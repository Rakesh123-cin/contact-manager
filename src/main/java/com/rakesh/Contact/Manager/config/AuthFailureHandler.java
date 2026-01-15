package com.rakesh.Contact.Manager.config;

import java.io.IOException;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.rakesh.Contact.Manager.Helpers.Message;
import com.rakesh.Contact.Manager.Helpers.MessageType;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthFailureHandler implements AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
                if(exception instanceof DisabledException)
                {
                    HttpSession session = request.getSession();
                    Message message = Message.builder()
                                            .content("Your account is disabled. Email has been sent to your registered email address for verification.")
                                            .type(MessageType.red)
                                            .build();
                    session.setAttribute("message", message);
                    response.sendRedirect("/login?disabled=true");
                }
                else
                {
                    response.sendRedirect("/login?error=true");
                }   
    }

}
