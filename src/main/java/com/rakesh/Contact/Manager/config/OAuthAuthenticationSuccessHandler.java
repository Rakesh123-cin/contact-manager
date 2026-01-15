package com.rakesh.Contact.Manager.config;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.rakesh.Contact.Manager.Entities.Providers;
import com.rakesh.Contact.Manager.Entities.User;
import com.rakesh.Contact.Manager.Helpers.AppConstants;
import com.rakesh.Contact.Manager.Repositories.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    @Autowired
    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logger.info("OAuthAuthenticationSuccessHandler");

        // Identify the provider
        var oAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
        String authorizedClientRegistrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info(authorizedClientRegistrationId);

        DefaultOAuth2User oAuth2User = (DefaultOAuth2User)authentication.getPrincipal();
        //oAuth2User.getAttributes().forEach((key,val)->logger.info(key+" : "+val));
        User user = User.builder()
                    .userId(java.util.UUID.randomUUID().toString())
                    .providerId(oAuth2User.getName())
                    .enabled(true)
                    .emailVerified(true)
                    .roles(List.of(AppConstants.ROLE))
                    .build();

        if(authorizedClientRegistrationId.equalsIgnoreCase("google"))
        {
            user.setEmail(oAuth2User.getAttribute("email").toString());
            user.setName(oAuth2User.getAttribute("name").toString());
            user.setProfilePic(oAuth2User.getAttribute("picture"));
            user.setAbout("This user is registered from google via OAuth2");
            user.setProvider(Providers.GOOGLE);
        }
        else if(authorizedClientRegistrationId.equalsIgnoreCase("github"))
        {
            if(oAuth2User.getAttribute("email").toString()==null)
            {
                user.setEmailVerified(false);
                user.setEmail(oAuth2User.getAttribute("login").toString()+"@gmail.com");
            }
            else
               user.setEmail(oAuth2User.getAttribute("email").toString());
            user.setName(oAuth2User.getAttribute("name").toString());
            user.setProfilePic(oAuth2User.getAttribute("avatar_url").toString());
            user.setAbout(oAuth2User.getAttribute("bio").toString());
            user.setProvider(Providers.GITHUB);
        }
        else{
            logger.info("OAuth2AuthenticationSuccessHandler: Unknown provier");
        }
        // save the user in the database

        User savedUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if(savedUser==null)
        {
            userRepository.save(user);
            logger.info("User saved.... ");
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }

}
