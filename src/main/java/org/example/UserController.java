//package org.example;
//
//
//
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/users")
//public class UserController {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserController(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @GetMapping
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @PostMapping("/signup")
//    public User createUser(@RequestBody User user) {
//        return userRepository.save(user);
//    }
//
//
//}
