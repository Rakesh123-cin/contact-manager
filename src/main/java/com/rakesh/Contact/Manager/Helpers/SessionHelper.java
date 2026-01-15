package com.rakesh.Contact.Manager.Helpers;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class SessionHelper {

    public static void removeMessage()
    {
        System.out.println("Removing message from session");
        RequestContextHolder.getRequestAttributes().removeAttribute("message", RequestAttributes.SCOPE_SESSION);    
    }
}
