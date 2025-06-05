package com.backend;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.backend.User.User;

@Service
public interface AuthService {
    Map<String,Object> saveUser(User user);
    Map<String,Object> updateUser(User user,String uuid, boolean isChangePassword);
    Map<String,Object> deleteUser(String email,String sessionUUID);
    Map<String,Object> loginUser(User user);
    Map<String,Object> logOutUser(String sessionUUID);
}
