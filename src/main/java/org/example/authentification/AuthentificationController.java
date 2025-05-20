package org.example.authentification;

import org.example.entities.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthentificationController {
    private AuthService authService;
    AuthentificationController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping(path="/signup")
    void signupUser( @RequestBody User user){
     authService.save(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        try {
            boolean success = authService.authenticate(request.getUsername(), request.getPassword());
            return "Login successful!";
        }
        catch(Exception e){
            return e.getMessage();
        }

    }

}
