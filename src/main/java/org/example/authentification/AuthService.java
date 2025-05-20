package org.example.authentification;


import org.example.entities.UserRepo;
import org.springframework.stereotype.Service;
import org.example.entities.User;
import org.example.entities.UserRepo;
@Service
public class AuthService implements AuthServiceInterface {
    private UserRepo userRepo;
    AuthService(UserRepo userRepo){
        this.userRepo=userRepo;

    }


    @Override
    public User save(User newUser) {
        return userRepo.save(newUser);
    }


    public boolean authenticate(String username, String rawPassword) throws FailedLogin {
          return userRepo.findByUsername(username)
                .map(user -> {
                    if (user.getEnc_password().equals(rawPassword)) { // TODO: replace with passwordEncoder.matches
                        return true;
                    } else {
                        throw new FailedLogin();
                    }
                })
                .orElseThrow(() -> new FailedLogin());
    }

}
