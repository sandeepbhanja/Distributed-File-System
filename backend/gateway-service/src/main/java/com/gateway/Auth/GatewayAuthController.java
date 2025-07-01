package com.gateway.Auth;

import com.gateway.External.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class GatewayAuthController {

    @Value("${auth.connectionIP}")
    private String authUrl;

    private final RestTemplate restTemplate;

    private final JedisPool jedisPool;

    public GatewayAuthController(JedisPool jedisPool, RestTemplate restTemplate) {
        this.jedisPool = jedisPool;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> logIn(@RequestBody User user) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            HttpEntity<User> httpEntity = new HttpEntity<>(user);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(authUrl + "login", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                if ((boolean) responseBody.get("success")) {
                    responseObject.put("success", true);
                    responseObject.put("message", responseBody.get("message").toString());
                    ResponseCookie createCookie = ResponseCookie.from("sessionId", responseBody.get("sessionUUID").toString())
                            .httpOnly(true)              // Prevent JavaScript access
                            .secure(true)                // Use HTTPS in production
                            .path("/")                   // Cookie is valid for entire domain
                            .maxAge(Duration.ofSeconds(1800)) // Set expiration
                            .sameSite("Strict")          // Prevent CSRF (adjust if needed)
                            .build();
                    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,createCookie.toString()).body(responseObject);
                } else {
                    responseObject.put("success", false);
                    responseObject.put("message", responseBody.get("message").toString());
                }
            } else {
                responseObject.put("success", false);
                responseObject.put("message", "Error while login");
            }
        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return new ResponseEntity<>(responseObject,HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            HttpEntity<User> httpEntity = new HttpEntity<>(user);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(authUrl + "register", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                if ((boolean) responseBody.get("success")) {
                    responseObject.put("success", true);
                    responseObject.put("message", responseBody.get("message").toString());
                    ResponseCookie createCookie = ResponseCookie.from("sessionId", responseBody.get("sessionUUID").toString())
                            .httpOnly(true)              // Prevent JavaScript access
                            .secure(true)                // Use HTTPS in production
                            .path("/")                   // Cookie is valid for entire domain
                            .maxAge(Duration.ofSeconds(1800)) // Set expiration
                            .sameSite("Strict")          // Prevent CSRF (adjust if needed)
                            .build();
                    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,createCookie.toString()).body(responseObject);
                } else {
                    responseObject.put("success", false);
                    responseObject.put("message", responseBody.get("message").toString());
                }
            } else {
                responseObject.put("success", false);
                responseObject.put("message", "Error while login");
            }
        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@CookieValue(value = "sessionId",required = false) String sessionUUID) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            if(sessionUUID==null){
                return ResponseEntity
                        .ok()
                        .body(Map.of("success",false,"message","Cookie not present"));
            }
            Map<String, String> body = new HashMap<>();
            body.put("sessionUUID", sessionUUID);
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(body);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(authUrl + "logout", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                if ((boolean) responseBody.get("success")) {
                    responseObject.put("success", true);
                    ResponseCookie deleteCookie = ResponseCookie.from("sessionId","")
                            .httpOnly(true)
                            .secure(true)
                            .path("/")                // must match original path
                            .sameSite("Strict")
                            .maxAge(0)                // 0 means "delete immediately"
                            .build();
                    return ResponseEntity
                            .ok()
                            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                            .body(responseObject);
                } else {
                    responseObject.put("success", false);
                    responseObject.put("message", responseBody.get("message").toString());
                }
            } else {
                responseObject.put("success", false);
                responseObject.put("message", "Error while login");
            }
        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> delete(@RequestBody Map<String,Object> requestBody) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            Map<String, String> body = new HashMap<>();
            body.put("sessionUUID", requestBody.get("sessionUUID").toString());
            body.put("email",requestBody.get("email").toString());
            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(body);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(authUrl + "delete", HttpMethod.DELETE, httpEntity, new ParameterizedTypeReference<Map<String, Object>>() {
            });
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                if ((boolean) responseBody.get("success")) {
                    responseObject.put("success", true);
                    ResponseCookie deleteCookie = ResponseCookie.from("sessionId","")
                            .httpOnly(true)
                            .secure(true)
                            .path("/")                // must match original path
                            .sameSite("Strict")
                            .maxAge(0)                // 0 means "delete immediately"
                            .build();
                    return ResponseEntity
                            .ok()
                            .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                            .body(responseObject);
                } else {
                    responseObject.put("success", false);
                    responseObject.put("message", responseBody.get("message").toString());
                }
            } else {
                responseObject.put("success", false);
                responseObject.put("message", "Error while login");
            }
        } catch (Exception e) {
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return new ResponseEntity<>(responseObject, HttpStatus.OK);
    }
}