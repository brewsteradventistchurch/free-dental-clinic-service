//package com.sda.dentalclinic.user.controller;
//
//import com.sda.dentalclinic.user.model.Role;
//import com.sda.dentalclinic.user.model.User;
//import com.sda.dentalclinic.user.repository.UserRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.time.Instant;
//
//@RestController
//@RequestMapping("/api/v1/users")
//public class UserController {
//
//    private final UserRepository userRepository;
//
//    public UserController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @PostMapping("/test")
//    @ResponseStatus(HttpStatus.CREATED)
//    public Mono<User> createTestUser(@RequestParam String email) {
//
//        User user = User.builder()
//                .email(email)
//                .fullName("Test User")
//                .role(Role.VOLUNTEER)
//                .approved(false)
//                .active(true)
//                .createdDate(Instant.now())
//                .build();
//
//        return userRepository.save(user);
//    }
//
//    @GetMapping("/test")
//    public Mono<User> findTestUser(@RequestParam String email) {
//        return userRepository.findByEmail(email);
//    }
//}