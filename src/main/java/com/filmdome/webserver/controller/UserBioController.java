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

    /**
     * Constructor injection for required dependencies.
     *
     * @param accountRepository Repository used to access user accounts
     * @param passwordEncoder Password encoder used for password verification
     */
    @Autowired
    public UserBioController(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads the information required by the
     * account settings page.
     *
     * This helper method prevents duplicate code
     * when the page needs to be redisplayed.
     *
     * @param model Model used to pass data to the view
     * @param user Current user
     */
    private void loadUser(Model model, User user) {

        // Convert the entity into a DTO for display.
        model.addAttribute("userView", UserUtil.convertTo(user));

        // Create an empty password form.
        model.addAttribute("passwordView", new PasswordView());
    }

    /**
     * Displays the currently logged-in user's
     * account information.
     *
     * @param user User stored in the current session
     * @param model Model used to pass data to the view
     * @return User account page
     */
    @GetMapping("/displayUserInfo")
    public String displayUserInfo(@SessionAttribute("user") UserDisplayDto user, Model model) {

        // Retrieve the latest account information from the database.
        User fetchedUser = accountRepository.findById(user.getId()).orElse(null);

        // Populate the model.
        loadUser(model, fetchedUser);

        return "user-update";
    }

    /**
     * Updates a user's account information or
     * deletes their account depending on which
     * button was pressed.
     *
     * Before saving, the controller verifies that
     * the email, username, and phone number remain
     * unique across all user accounts.
     *
     * @param userDto Updated account information
     * @param bindingResult Validation results
     * @param model Model used to pass data to the view
     * @param updateButton Indicates the Update button was pressed
     * @param deleteButton Indicates the Delete button was pressed
     * @return Updated account page or login page after deletion
     */
    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@Valid @ModelAttribute("userView") UserDto userDto, BindingResult bindingResult,
                                 Model model, @RequestParam(required = false) String updateButton, @RequestParam(required = false) String deleteButton) {

        // Retrieve the existing account.
        User existingUser = accountRepository.findById(userDto.getId()).orElse(null);

        // Reset the password form.
        model.addAttribute("passwordView", new PasswordView());

        // Stop processing if validation fails.
        if (bindingResult.hasErrors()) {
            return "user-update";
        }

        if (updateButton != null) {

            // Verify that the email address is unique.
            if (!userDto.getEmail().equals(existingUser.getEmail())) {

                User emailUser =
                        accountRepository.findByEmail(userDto.getEmail());

                if (emailUser != null && emailUser.getId() != existingUser.getId()) {
                    model.addAttribute("userInputErrors", "Email already exists.");
                    loadUser(model, existingUser);

                    return "user-update";
                }
            }

            // Verify that the username is unique.
            if (!userDto.getUsername().equals(existingUser.getUsername())) {

                User usernameUser = accountRepository.findByUsername(userDto.getUsername());

                if (usernameUser != null && usernameUser.getId() != existingUser.getId()) {
                    model.addAttribute("userInputErrors","Username already exists.");
                    loadUser(model, existingUser);

                    return "user-update";
                }
            }

            // Verify that the phone number is unique.
            if (!userDto.getPhoneNumber().equals(existingUser.getPhoneNumber())) {

                User phoneUser = accountRepository.findByPhoneNumber(userDto.getPhoneNumber());

                if (phoneUser != null && phoneUser.getId() != existingUser.getId()) {
                    model.addAttribute("userInputErrors", "Phone number already exists.");
                    loadUser(model, existingUser);

                    return "user-update";
                }
            }

            // Copy the updated information into the entity.
            existingUser.setFirstName(userDto.getFirstName());
            existingUser.setLastName(userDto.getLastName());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setUsername(userDto.getUsername());
            existingUser.setPhoneNumber(userDto.getPhoneNumber());

            // Save the updated account.
            accountRepository.save(existingUser);

            // Display a success message.
            model.addAttribute("updateAccountSuccess", true);
            model.addAttribute("updateAccountMsg", "Your account has been updated!");

            loadUser(model, existingUser);

            return "user-update";
        }

        // User selected Delete.
        if (deleteButton != null) {

            // Remove the account from the database.
            accountRepository.deleteById(userDto.getId());

            // Return to the login page.
            return "redirect:/displayLoginPage";
        }

        loadUser(model, existingUser);

        return "user-update";
    }

    /**
     * Updates the user's account password.
     *
     * The controller verifies the user's current
     * password before replacing it with a newly
     * encrypted password.
     *
     * @param passwordView Password form
     * @param result Validation results
     * @param model Model used to pass data to the view
     * @return User account page
     */
    @PostMapping("/updateUserPassword")
    public String updateUserPassword(@Valid @ModelAttribute PasswordView passwordView, BindingResult result, Model model) {

        // Retrieve the user's account.
        User user = accountRepository.findById(passwordView.getId()).orElse(null);

        // If the account no longer exists,
        // redirect to the login page.
        if (user == null) {
            return "redirect:/login";
        }

        // Reload the account page.
        model.addAttribute("userView", UserUtil.convertTo(user));
        model.addAttribute("passwordView", new PasswordView());

        // Stop processing if validation fails.
        if (result.hasErrors()) {
            return "user-update";
        }

        // Verify the current password.
        if (!passwordEncoder.matches(passwordView.getCurrentPassword(), user.getPassword())) {

            model.addAttribute("passwordError", "Current password is incorrect");
            return "user-update";
        }

        // Verify the new passwords match.
        if (!passwordView.getNewPassword().equals(passwordView.getConfirmPassword())) {

            model.addAttribute("passwordError", "New passwords do not match");
            return "user-update";
        }

        // Encrypt and save the new password.
        user.setPassword(passwordEncoder.encode(passwordView.getNewPassword()));

        accountRepository.save(user);

        // Display a success message.
        model.addAttribute("updatePasswordSuccess", true);
        model.addAttribute("updatePasswordMsg", "Your password has been updated!");
        model.addAttribute("userView", UserUtil.convertTo(user));
        model.addAttribute("passwordView", new PasswordView());

        return "user-update";
    }
}