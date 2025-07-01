package com.gateway.File;

import com.gateway.File.Service.GatewayFileService;
import com.gateway.Utils.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import redis.clients.jedis.JedisPool;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/file")
public class GatewayFileController {

    @Value("${chunk.connectionIP}")
    private String chunkUrl;

    private final RestTemplate restTemplate;
    private final SessionService sessionService;
    private final GatewayFileService gatewayFileService;
    private final WebClient webClient;

    public GatewayFileController(WebClient.Builder builder,RestTemplate restTemplate,SessionService sessionService,GatewayFileService gatewayFileService) {
        this.sessionService = sessionService;
        this.restTemplate = restTemplate;
        this.gatewayFileService = gatewayFileService;
        this.webClient = builder.baseUrl("http://chunk-service.default:8082/chunk/").build();
    }

    @PostMapping("/store-chunk")
    public ResponseEntity<Map<String,Object>> uploadChunks(
            @RequestPart("fileName") String fileName,
            @RequestPart("chunkId") String chunkId,
            @RequestPart("chunkData") MultipartFile chunkData,
            @CookieValue(value = "sessionId", required = false) String sessionUUID) {

        try {
            if (sessionUUID == null) {
                return ResponseEntity.ok().body(Map.of("success", false, "message", "Session expired"));
            }

            JSONObject isSessionExist = this.sessionService.checkIfSessionExist(sessionUUID);
            if (!isSessionExist.getBoolean("success")) {
                return ResponseEntity.ok().body(Map.of("success", false, "message", "Invalid session"));
            }

            Map<String,Object> response = this.gatewayFileService.storeChunk(fileName,chunkId, chunkData.getInputStream(),isSessionExist.getString("sessionData"));

            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", multipartFile.getResource());


//            Map<String, Object> response = webClient.post()
//                    .uri("store-chunk")
//                    .contentType(MediaType.MULTIPART_FORM_DATA)
//                    .body(BodyInserters.fromMultipartData(builder.build()))
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
//                    .block();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.ok().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

}
