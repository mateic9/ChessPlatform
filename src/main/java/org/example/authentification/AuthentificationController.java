package org.example.authentification;

import org.example.entities.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
//import org.springframework.web.bind.annotation.Valid;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
public class AuthentificationController {
    private AuthService authService;

    AuthentificationController(AuthService authService){
        this.authService=authService;
    }
    @PostMapping(path="/signup")
    ResponseEntity<Map<String,Object>> signupUser( @RequestBody User user) throws FailedLogin{
       try {
           authService.save(user);
           return ResponseEntity.ok(Map.of(
                   "success",true,
                   "message","Signed up succesfully"
           )
           );
       }
       catch (DataIntegrityViolationException e) {
           System.out.println("Data integrity violation exception");
           return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                   "success", false,
                   "message", "Username already exists"
           ));
       }
       catch (Exception e){
           return ResponseEntity.ok(Map.of(
                   "success",false,
                   "message", e.getMessage()
           )
           );
       }
    }

    @PostMapping("/login")

    public ResponseEntity<Map<String,Object>> login(@RequestBody LoginRequest request) throws FailedLogin{
//        Optional<User> userOpt = authService.getUserRepo().findByUsername(request.getUsername());
       try {
//           if (userOpt.isEmpty())
//               throw new FailedLogin("No user found with name:"+request.getUsername());
//
//           User user = userOpt.get();
//
//           // Check password manually — NOTE: This is plaintext comparison for demo purposes only.
//           if (!user.getEnc_password().equals(request.getPassword()))
//               throw new FailedLogin("Invalid username or password");
             Long idPlayer=authService.authenticate(request);

           return ResponseEntity.ok(Map.of(
                   "success", true,
                   "id", idPlayer,
                   "message", "Login successful"
           ));
       }
       catch(FailedLogin exc){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                   "success",false,
                   "message",exc.getMessage())
           );
       }
       catch(Exception exc){
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                   "success",false,
                   "message","Internal error: "+exc.getMessage())
           );
       }
    }

//    public void notifyClient(Long idPlayer, Map<String, Object> jsonBody) {
//        ClientInfo client = clientMap.get(idPlayer);
//        if (client == null) {
//            System.err.println("Client not found for ID: " + idPlayer);
//            return;
//        }
//
//        String url = "http://" + client.getIp() + ":" + client.getPort() + "/receive-message";
//
//        try {
//            RestTemplate restTemplate = new RestTemplate();
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
//            HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonBody, headers);
//
//            restTemplate.postForEntity(url, request, String.class);
//        } catch (Exception e) {
//            System.err.println("Failed to notify client " + idPlayer + ": " + e.getMessage());
//        }
//    }


}
