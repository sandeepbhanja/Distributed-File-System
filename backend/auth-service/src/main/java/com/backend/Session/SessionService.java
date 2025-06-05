package com.backend.Session;

import com.backend.User.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class SessionService {

    private final JedisPool jedisPool;

    public SessionService(JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    @Value("${redis.sessionTimeOutInSeconds}")
    String sessionTimeOutInSeconds;

    public Map<String,Object> createUserSession(User user){
        Map<String,Object> sessionObject = new HashMap<>();
        String sessionUUID = String.valueOf(UUID.randomUUID());
        try{
            try(Jedis jedis = jedisPool.getResource()){
                jedis.setex(sessionUUID,Long.parseLong(sessionTimeOutInSeconds),user.getEmail());
                sessionObject.put("success",true);
                sessionObject.put("sessionUUID",sessionUUID);
            }
            catch (Exception e){
                e.printStackTrace();
                sessionObject.put("success",false);
                sessionObject.put("message",e.getMessage());
            }
        }
        catch(Exception e){
            e.printStackTrace();
            sessionObject.put("success",false);
            sessionObject.put("message",e.getMessage());
        }
        return sessionObject;
    }

    public boolean deleteRedisSession(String sessionUUID){
        boolean isSessionDeleted = false;
        try{
            try(Jedis jedis = jedisPool.getResource()){
                jedis.del(sessionUUID);
                isSessionDeleted = true;
            }
            catch (Exception e){
                e.printStackTrace();
                isSessionDeleted = false;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            isSessionDeleted = false;
        }
        return isSessionDeleted;
    }
}
