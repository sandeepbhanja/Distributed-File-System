package com.backend;

import com.backend.User.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController{

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/login")
    ResponseEntity<Map<String,Object>> LoginUser(@RequestBody User user){
        try{
            Map<String,Object> userObject = this.authService.loginUser(user);
            return new ResponseEntity<>(userObject, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    ResponseEntity<Map<String,Object>> registerUser(@RequestBody User user){
        try{
            Map<String,Object> userObject = this.authService.saveUser(user);
            return new ResponseEntity<>(userObject, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    ResponseEntity<Map<String,Object>> updateUser(@RequestBody Map<String,Object> requestBody){
        try{
            User user = (User) requestBody.get("user");
            boolean isPasswordChanged = (boolean) requestBody.get("isPasswordChanged");
            Map<String,Object> userObject = this.authService.updateUser(user,user.getId(),isPasswordChanged);
            return new ResponseEntity<>(userObject, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    ResponseEntity<Map<String,Object>> deleteUser(@RequestBody Map<String,Object> requestBody){
        try{
            String email = (String)requestBody.get("email");
            String sessionUUID = requestBody.get("sessionUUID").toString();
            Map<String,Object> userObject = this.authService.deleteUser(email,sessionUUID);
            return new ResponseEntity<>(userObject, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/logout")
    ResponseEntity<Map<String,Object>> logOutUser(@RequestBody String sessionUUID){
        try{
            Map<String,Object> userObject = this.authService.logOutUser(sessionUUID);
            return new ResponseEntity<>(userObject, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(Map.of("message",e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
