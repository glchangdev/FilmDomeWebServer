package com.filmdome.webserver.controller;

import com.filmdome.webserver.entity.User;
import com.filmdome.webserver.repository.AccountRepository;
import com.filmdome.webserver.model.Login;
import com.filmdome.webserver.util.UserUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor injection for required dependencies.
     *
     * @param accountRepository Repository used to access user accounts
     * @param passwordEncoder   Password encoder used for password verification
     */
    @Autowired
    public LoginController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Redirects users who visit the root URL ("/")
     * to the application's login page.
     *
     * @return Redirect to the login page
     */
    @GetMapping("/")
    public String redirectToLogin() {

        // Redirect the browser to the login page.
        return "redirect:/displayLoginPage";
    }

    /**
     * Displays the login page.
     *
     * If the user's session expired while attempting to
     * access a protected page, an optional request parameter
     * is used to display an informational message.
     *
     * @param expired Optional flag indicating the session expired
     * @param model   Model used to pass data to the view
     * @return The login page
     */
    @GetMapping("/displayLoginPage")
    public String displayLoginPage(@RequestParam(required = false) String expired, Model model) {

        // Create an empty Login object for Thymeleaf form binding.
        model.addAttribute("userLogin", new Login());

        // Display a session expired message if redirected here by the SessionInterceptor.
        if (expired != null) {
            model.addAttribute("sessionExpired", true);
        }

        return "user-login";
    }

    /**
     * Logs the current user out by invalidating their session
     * and returning them to the login page.
     *
     * @param session Current HTTP session
     * @param theModel Model used to populate the login page
     * @return The login page
     */
    @GetMapping("/sessionEndLogout")
    public String sessionEndLogout(HttpSession session, Model theModel) {

        // Destroy the current session if one exists.
        if (session != null) {
            session.invalidate();
        }

        // Create a fresh Login object for the login form.
        theModel.addAttribute("userLogin", new Login());

        return "user-login";
    }

    /**
     * Processes a login request.
     *
     * Users may log in using either their username,
     * email address, or phone number.
     *
     * If authentication succeeds, a lightweight user DTO
     * is stored in the session and the user is redirected
     * to the home page.
     *
     * If authentication fails, the login page is redisplayed
     * with an error message.
     *
     * @param userLogin Login credentials submitted by the user
     * @param model     Model used to pass data back to the view
     * @param session   Current HTTP session
     * @return Redirect to the home page on success, otherwise the login page
     */
    @PostMapping("/login")
    public String login(@ModelAttribute Login userLogin, Model model, HttpSession session) {

        // Retrieve the user's login credentials.
        String input = userLogin.getLoginInput();
        String password = userLogin.getPassword();

        // Attempt to locate the account using the supplied username.
        User user = accountRepository.findByUsername(input);

        // If not found, attempt to locate the account by email.
        if (user == null) {
            user = accountRepository.findByEmail(input);
        }

        // If still not found, attempt to locate the account by phone number.
        if (user == null) {
            user = accountRepository.findByPhoneNumber(input);
        }

        // No matching account exists.
        if (user == null) {
            model.addAttribute("userLogin", userLogin);
            model.addAttribute("errorMessage", "Your login credentials are incorrect.");
            return "user-login";
        }

        // Compare the submitted password with the encrypted password stored in the database.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("userLogin", userLogin);
            model.addAttribute("errorMessage", "Your login credentials are incorrect.");
            return "user-login";
        }

        // Store a DTO in the session instead of the full entity. Then return to a redirect:/displayHomePage
        session.setAttribute("user", UserUtil.convertToDisplayDto(user));
        return "redirect:/displayHomePage";
    }
}