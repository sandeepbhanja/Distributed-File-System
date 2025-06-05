package com.backend.AuthImplementation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import com.backend.Session.SessionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.AuthRepository;
import com.backend.AuthService;
import com.backend.User.User;

@Service
public class AuthServiceImplementation implements AuthService {

    private final AuthRepository authRepository;
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private final SessionService sessionService;
    public AuthServiceImplementation(AuthRepository authRepository, SessionService sessionService) {
        this.authRepository = authRepository;
        this.sessionService = sessionService;
    }

    public Map<String, Object> saveUser(User user) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            if (this.authRepository.findUserByEmail(user.getEmail()) == null) {
                String encodedPassword = encoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                this.authRepository.save(user);
                Map<String,Object> sessionObject = this.sessionService.createUserSession(user);
                if((boolean) sessionObject.get("success")){
                    responseObject.put("success", true);
                    responseObject.put("message", "User created Successfully and Session Created");
                    responseObject.put("sessionUUID", sessionObject.get("sessionUUID"));
                }
                else{
                    responseObject.put("success", false);
                    responseObject.put("message", sessionObject.get("message").toString());
                }
            } else {
                responseObject.put("success", false);
                responseObject.put("message", "Email already in use");
            }

        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return responseObject;
    }

    public Map<String, Object> updateUser(User user, String uuid, boolean isChangePassword) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            Optional<User> existingUserData = this.authRepository.findById(uuid);
            if (existingUserData.isEmpty()) {
                responseObject.put("success", false);
                responseObject.put("message", "User does not exist");
            } else {
                User updatedUserData = existingUserData.get();
                updatedUserData.setEmail(user.getEmail() != null ? user.getEmail() : updatedUserData.getEmail());
                responseObject.put("success", true);
                responseObject.put("message", "User Updated");
            }

        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return responseObject;
    }

    public Map<String, Object> deleteUser(String email,String sessionUUID) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            User existingUserData = this.authRepository.findUserByEmail(email);
            if (existingUserData == null) {
                responseObject.put("success", false);
                responseObject.put("message", "User does not exist");
            } else {
                this.authRepository.deleteById(existingUserData.getId());
                boolean isSessionDeleted = this.sessionService.deleteRedisSession(sessionUUID);
                responseObject.put("success", true);
                responseObject.put("message", "User Deleted");
            }
        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return responseObject;
    }

    public Map<String,Object> loginUser(User user){
        Map<String, Object> responseObject = new HashMap<>();
        try{
            User existingUser = this.authRepository.findUserByEmail(user.getEmail());
            if(existingUser == null){
                responseObject.put("success", false);
                responseObject.put("message", "Either EmailId or Password is incorrect");
            }
            else{
                boolean isPasswordMatch = encoder.matches(user.getPassword(),existingUser.getPassword());
                if(!isPasswordMatch){
                    responseObject.put("success", false);
                    responseObject.put("message", "Either EmailId or Password is incorrect");
                }
                else{
                    Map<String,Object> sessionObject = this.sessionService.createUserSession(existingUser);
                    if((boolean) sessionObject.get("success")){
                        responseObject.put("success", true);
                        responseObject.put("message", "User validated Successfully and Session Created");
                        responseObject.put("sessionUUID", sessionObject.get("sessionUUID"));
                    }
                    else{
                        responseObject.put("success", false);
                        responseObject.put("message", sessionObject.get("message").toString());
                    }
                }
            }
        }
        catch (Exception e){
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return responseObject;
    }

    public Map<String,Object> logOutUser(String sessionUUID){
        Map<String, Object> responseObject = new HashMap<>();
        try {
            boolean isDeleted = this.sessionService.deleteRedisSession(sessionUUID);
            responseObject.put("success", isDeleted);
        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return responseObject;
    }
}
