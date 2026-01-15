package com.rakesh.Contact.Manager.Helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class Helper
 {
    public static String getEmailOfLoggedInUser(Authentication authentication)
    {

        if(authentication instanceof OAuth2AuthenticationToken)
        {
            var oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
            String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
            if(authorizedClientRegistrationId.equalsIgnoreCase("google"))
            {
                return oAuth2User.getAttribute("email").toString();
            }
            else if(authorizedClientRegistrationId.equalsIgnoreCase("github"))
            {
                String email = oAuth2User.getAttribute("email").toString()!=null ? oAuth2User.getAttribute("email").toString() 
                                                                        : oAuth2User.getAttribute("login").toString()+"@gmail.com";
                return email;
            }
        }
    
        return authentication.getName();
        
    }

    public static String getEmailVerificationLink(String emailVerificationToken)
    {
        return "https://contactmanager.store/auth/verify-email?token=" + emailVerificationToken;
    }
}
