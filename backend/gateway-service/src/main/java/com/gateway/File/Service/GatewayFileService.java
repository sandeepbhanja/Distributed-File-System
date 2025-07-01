package com.gateway.File.Service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Service
public class GatewayFileService {

    private static final String ollamaURL = "http://ollama-service.default:11434/api/embeddings";
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> storeChunk(String fileName, String chunkId, InputStream chunkStream, String username) {
        Map<String, Object> responseObject = new HashMap<>();
        try {
            File chunkDirectory = new File("/mnt/chunk/" + username + "/" + fileName);
            if (!chunkDirectory.exists()) {
                chunkDirectory.mkdirs();
            }
            File chunkFile = new File(chunkDirectory, fileName + "_chunkId_" + chunkId);
            Files.copy(chunkStream, chunkFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Path chunkPath = Paths.get(chunkDirectory + "/" + fileName + "_chunkId_" + chunkId);

            String content = Files.readString(chunkPath);

//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "nomic-embed-text");
            requestBody.put("prompt", content);
//
//            HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(requestBody,headers);
//
//            ResponseEntity<Map<String,Object>> response = restTemplate.exchange(ollamaURL, HttpMethod.POST, httpEntity, new ParameterizedTypeReference<Map<String,Object>>() {
//            });

            WebClient client = WebClient.create(ollamaURL);
            Mono<Map> entityMono = client.post()
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .exchangeToMono(response -> {
                        if (response.statusCode().equals(HttpStatus.OK)) {
                            return response.bodyToMono(Map.class);
                        } else {
                            return response.createError();
                        }
                    });
            Map<String, Object> embeddingMapping = new HashMap<>();
            entityMono.subscribe(embeddingMapping::putAll);

            System.out.println("EmbeddingMapping===" + embeddingMapping);

            responseObject.put("success", true);

        } catch (Exception e) {
            e.printStackTrace();
            responseObject.put("success", false);
            responseObject.put("message", e.getMessage());
        }
        return responseObject;
    }

}
