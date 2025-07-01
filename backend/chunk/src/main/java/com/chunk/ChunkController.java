package com.chunk;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chunk")
public class ChunkController {

    private final ChunkService chunkService;

    public ChunkController(ChunkService chunkService){
        this.chunkService = chunkService;
    }

    @PostMapping(value = "/store-chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,Object>> storeChunk(@RequestPart("fileName") String fileName, @RequestPart("chunkId") String chunkId, @RequestPart("chunkPart") MultipartFile chunkPart){
        Map<String,Object> responseObject = new HashMap<>();
        try{
            InputStream chunkStream = chunkPart.getInputStream();
            Map<String,Object> storeChunkObject = this.chunkService.storeChunk(fileName,chunkId,chunkStream);
            if((boolean)storeChunkObject.get("success")){
                responseObject.put("success",true);
                responseObject.put("message","Chunk stored");
            }
            else{
                responseObject.put("success",false);
                responseObject.put("message",responseObject.get("message").toString());
            }
        }
        catch(Exception e){
            responseObject.put("success",false);
            responseObject.put("message",e.getMessage());
        }

        return ResponseEntity.ok().body(responseObject);
    }

    @PostMapping("/mergeChunks")
    public ResponseEntity<Resource> mergeChunks(@RequestBody Map<String,Object> requestBody){
        try{
            String fileName = requestBody.get("fileName").toString();
            Map<String,Object> mergeChunkObject = this.chunkService.mergeChunks(fileName);
            if((boolean)mergeChunkObject.get("success")){
                File downloadFile = new File(mergeChunkObject.get("filePath").toString());
                InputStreamResource resource = new InputStreamResource(new FileInputStream(downloadFile));
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .contentLength(downloadFile.length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
            else{
                return ResponseEntity.internalServerError().body(null);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
