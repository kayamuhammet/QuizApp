package com.intershipProject.QuizApp.Controller;

import com.intershipProject.QuizApp.Model.User;
import com.intershipProject.QuizApp.Repository.UserRepo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {
    @Autowired
    private UserRepo userRepo;
//    @GetMapping(value = "/admin-dashboard")
//    public ModelAndView getPage(){
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("view/html/admin/dashboard");
//        return modelAndView;
//    }

    @PostMapping("/register-users")
    public String registerUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorBuilder = new StringBuilder();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorBuilder.append(error.getField())
                        .append(": ")
                        .append(error.getDefaultMessage())
                        .append("; ");
            }
            return "Validation errors: " + errorBuilder.toString();
        }

        // Hash the password using BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);

        userRepo.save(user);
        return "User registered successfully.";
    }

}
