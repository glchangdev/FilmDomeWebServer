package com.filmdome.webserver.controller;

import com.filmdome.webserver.dto.UserDisplayDto;
import com.filmdome.webserver.dto.UserDto;
import com.filmdome.webserver.entity.User;
import com.filmdome.webserver.model.PasswordView;
import com.filmdome.webserver.repository.AccountRepository;
import com.filmdome.webserver.util.UserUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserBioController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserBioController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private void loadUser(Model model, User user) {
        model.addAttribute("userView", UserUtil.convertTo(user));
        model.addAttribute("passwordView", new PasswordView());
    }

    @GetMapping("/displayUserInfo")
    public String displayUserInfo(@SessionAttribute("user") UserDisplayDto user, Model model) {

        User fetchedUser = accountRepository.findById(user.getId()).orElse(null);
        loadUser(model, fetchedUser);

        return "user-update";
    }

    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@Valid @ModelAttribute("userView") UserDto userDto, BindingResult bindingResult, Model model,
            @RequestParam(required = false) String updateButton, @RequestParam(required = false) String deleteButton) {

        User existingUser = accountRepository.findById(userDto.getId()).orElse(null);
        model.addAttribute("passwordView", new PasswordView());

        if (bindingResult.hasErrors()) {
            return "user-update";
        }

        if (updateButton != null) {

            if (!userDto.getEmail().equals(existingUser.getEmail())) {
                User emailUser = accountRepository.findByEmail(userDto.getEmail());
                if (emailUser != null && emailUser.getId() != existingUser.getId()) {
                    model.addAttribute("userInputErrors", "Email already exists.");
                    loadUser(model, existingUser);
                    return "user-update";
                }
            }

            if (!userDto.getUsername().equals(existingUser.getUsername())) {
                User usernameUser = accountRepository.findByUsername(userDto.getUsername());
                if (usernameUser != null && usernameUser.getId() != existingUser.getId()) {
                    model.addAttribute("userInputErrors", "Username already exists.");
                    loadUser(model, existingUser);
                    return "user-update";
                }
            }

            if (!userDto.getPhoneNumber().equals(existingUser.getPhoneNumber())) {
                User phoneUser = accountRepository.findByPhoneNumber(userDto.getPhoneNumber());
                if (phoneUser != null && phoneUser.getId() != existingUser.getId()) {
                    model.addAttribute("userInputErrors", "Phone number already exists.");
                    loadUser(model, existingUser);
                    return "user-update";
                }
            }

            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setUsername(userDto.getUsername());
            existingUser.setPhoneNumber(userDto.getPhoneNumber());

            accountRepository.save(existingUser);

            model.addAttribute("updateAccountSuccess", true);
            model.addAttribute("updateAccountMsg", "Your account has been updated!");

            loadUser(model, existingUser);
            return "user-update";
        }

        if (deleteButton != null) {
            accountRepository.deleteById(userDto.getId());
            return "redirect:/displayLoginPage";
        }

        loadUser(model, existingUser);
        return "user-update";
    }

    @PostMapping("/updateUserPassword")
    public String updateUserPassword(@Valid @ModelAttribute PasswordView passwordView, BindingResult result, Model model) {

        User user = accountRepository.findById(passwordView.getId()).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("userView", UserUtil.convertTo(user));
        model.addAttribute("passwordView", new PasswordView());

        if (result.hasErrors()) {
            return "user-update";
        }

        if (!passwordEncoder.matches(passwordView.getCurrentPassword(), user.getPassword())) {
            model.addAttribute("passwordError", "Current password is incorrect");
            return "user-update";
        }

        if (!passwordView.getNewPassword().equals(passwordView.getConfirmPassword())) {
            model.addAttribute("passwordError", "New passwords do not match");
            return "user-update";
        }

        user.setPassword(passwordEncoder.encode(passwordView.getNewPassword()));
        accountRepository.save(user);

        model.addAttribute("updatePasswordSuccess", true);
        model.addAttribute("updatePasswordMsg", "Your password has been updated!");
        model.addAttribute("userView", UserUtil.convertTo(user));
        model.addAttribute("passwordView", new PasswordView());

        return "user-update";
    }
}