package com.gateway.Utils;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class SessionService {

    private final JedisPool jedisPool;
    public SessionService(JedisPool jedisPool){
        this.jedisPool = jedisPool;
    }

    public JSONObject checkIfSessionExist(String sessionUUID){
        JSONObject responseObject = new JSONObject();
        try(Jedis jedis = jedisPool.getResource()){
            if(jedis.exists(sessionUUID)){
                responseObject.put("success",true);
                responseObject.put("sessionData",jedis.get(sessionUUID));
            }
            else {
                responseObject.put("success", false);
            }
        } catch (Exception e) {
            responseObject.put("success",false);
            responseObject.put("sessionData",e.getMessage());
        }
        return responseObject;
    }
}
