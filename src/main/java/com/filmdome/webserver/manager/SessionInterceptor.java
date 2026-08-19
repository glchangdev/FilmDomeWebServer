package com.filmdome.webserver.manager;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class SessionInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();

        if (uri.equals("/")
                || uri.equals("/login")
                || uri.equals("/displayLoginPage")
                || uri.equals("/displayGuestHomePage")
                || uri.equals("/displaySignUpPage")
                || uri.equals("/processUserSignUp")
                || uri.equals("/sessionEndLogout")
                || uri.equals("/favicon.ico")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/webjars/")) {

            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {

            response.sendRedirect(request.getContextPath() + "/displayLoginPage?expired=true");
            return false;
        }

        return true;
    }
}