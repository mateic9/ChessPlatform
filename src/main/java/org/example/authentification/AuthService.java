package org.example.authentification;


import lombok.Getter;
import org.example.entities.UserRepo;
import org.springframework.stereotype.Service;
import org.example.entities.User;
import org.example.entities.UserRepo;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Service
public class AuthService implements AuthServiceInterface {
    @Getter
    private UserRepo userRepo;
    @Getter
    private Map<Long,Integer> userAddressMap =new HashMap<Long,Integer>();
    AuthService(UserRepo userRepo){
        this.userRepo=userRepo;

    }


    @Override
    public User save(User newUser) {
        return userRepo.save(newUser);
    }


    public Long authenticate(LoginRequest request)throws FailedLogin {

          Optional<User>   userOpt=userRepo.findByUsername(request.getUsername());
          if(userOpt.isEmpty())
              throw new FailedLogin("No user with this username: "+request.getUsername());
          if(!userOpt.get().getEnc_password().equals(request.getPassword()))
              throw new FailedLogin("Credentials don't match");
          long idPlayer= userOpt.get().getId();
          userAddressMap.put(idPlayer, request.getPort());
          return idPlayer;

    }



}
