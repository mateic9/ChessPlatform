package org.example.authentification;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.awt.SystemColor.text;
//import org.springframework.web.bind.annotation.Valid;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/auth")
@ComponentScan
public class AuthentificationController {
    private AuthService authService;

    private JavaMailSender emailSender;

    AuthentificationController(AuthService authService,JavaMailSender sender){
        this.authService=authService;
        this.emailSender=sender;
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
    @PostMapping("/get-access-account")
    public ResponseEntity<Map<String,Object>> getAccessAccount(@RequestBody RecoverAccountAccessRequest request){
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        try{
            authService.checkRecoveryAccountCredentials(request);
            sendPasswordRecoveryEmail(request.getEmail(),request.getPort());
            jsonBody.put("success",true);
            jsonBody.put("message","Am trimis link pe adresa de gmail");
            return ResponseEntity.status(200).body(jsonBody);
        }
        catch(MessagingException e){
            jsonBody.put("success",false);
            jsonBody.put("message","Eroare la trimiterea mesajului!");
            return  ResponseEntity.status(500).body(jsonBody);

        }
        catch(Exception e){
            jsonBody.put("success",false);
         jsonBody.put("message",e.getMessage());
          return ResponseEntity.status(400).body(jsonBody);
        }
    }

    private void sendPasswordRecoveryEmail(String to,int port) throws MessagingException {
        final String recoveryLink="http://localhost:"+port+"/#"+"/reset_password_page";
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("fepa424@gmail.com");
        helper.setTo(to);
        helper.setSubject("Recuperare parolă Chess.com");

        String text = "<p>Bună,</p>" +
                "<p>Ai cerut recuperarea parolei contului tău Chess.com.</p>" +
                "<p>Dă clic pe linkul de mai jos pentru a reseta parola:</p>" +
                "<p><a href=\"" + recoveryLink + "\">Recuperează parola</a></p>" +
                "<p>Dacă nu ai făcut această solicitare, poți ignora acest email.</p>" +
                "<p>Mulțumim!</p>";

        helper.setText(text, true); // true means HTML

        emailSender.send(message);
    }
 @PostMapping("/new-password")
 public  ResponseEntity<Map<String,Object>>   changePassword(@RequestBody ChangePasswordRequest request){
        Map<String,Object> jsonBody=new HashMap<String,Object>();
        try {
            authService.changePassword(request);
            jsonBody.put("success",true);
            jsonBody.put("message","Changed password");
            return  ResponseEntity.status(200).body(jsonBody);
        }
        catch(Exception e){
            jsonBody.put("success",false);
            jsonBody.put("message",e.getMessage());
            return ResponseEntity.status(400).body(jsonBody);
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
