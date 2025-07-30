package org.ishika.demo_security.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = "/";

        for (GrantedAuthority auth : authorities) {
            if (auth.getAuthority().equals("ROLE_PATIENT")) {
                redirectUrl = "/patient/dashboard";
                break;
            } else if (auth.getAuthority().equals("ROLE_DOCTOR")) {
                redirectUrl = "/doctor/dashboard";
                break;
            }
        }
        try {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
