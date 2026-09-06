package com.filmdome.webserver.controller;

import com.filmdome.webserver.dto.UserDto;
import com.filmdome.webserver.repository.AccountRepository;
import com.filmdome.webserver.entity.User;
import com.filmdome.webserver.util.UserUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignUpController {
    
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignUpController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/displaySignUpPage")
    public String displaySignUpPage(Model theModel) {
        theModel.addAttribute("user", new UserDto());

        return "user-sign-up";
    }

    @PostMapping("/processUserSignUp")
    public String processUserSignUp(@Valid @ModelAttribute UserDto user, BindingResult result, Model model, HttpSession session) {

        System.out.println("===== SignUpController reached =====");
        boolean errorFound = false;

        if (result.hasErrors()) {
            model.addAttribute("user", new UserDto());
            errorFound = true;
        }

        if (user.getEmail() != null) {
            User emailUser = accountRepository.findByEmail(user.getEmail());
            if (emailUser != null) {
                model.addAttribute("emailErrorMessage", "Email already exists.");
                errorFound = true;
            }
        }

        if (user.getUsername() != null) {
            User usernameUser = accountRepository.findByUsername(user.getUsername());
            if (usernameUser != null) {
                model.addAttribute("usernameErrorMessage", "Username already exists.");
                errorFound = true;
            }
        }

        if (user.getPhoneNumber() != null){
            User phonenumberUser = accountRepository.findByPhoneNumber(user.getPhoneNumber());
            if (phonenumberUser != null) {
                model.addAttribute("phoneNumberErrorMessage", "Phone Number already exists.");
                errorFound = true;
            }
        }

        if (errorFound){
            return "user-sign-up";
        } else {

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            User userEntity = UserUtil.convertTo(user);
            accountRepository.save(userEntity);
            session.setAttribute("user", UserUtil.convertToDisplayDto(userEntity));

            return "redirect:/displayHomePage";
        }
    }
}
