package com.chunk.Implementation;

import com.chunk.ChunkService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChunkServiceImplementation implements ChunkService {

    public Map<String,Object> storeChunk(String fileName, String chunkId, InputStream chunkStream){
        Map<String,Object> responseObject = new HashMap<>();
        try{
            File chunkDirectory = new File("/mnt/chunk/"+fileName);
            if(!chunkDirectory.exists()){
                chunkDirectory.mkdirs();
            }
            File chunkFile = new File(chunkDirectory,fileName+"_chunkId_"+chunkId);

            Files.copy(chunkStream, chunkFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            responseObject.put("success",true);

        }
        catch (Exception e){
            responseObject.put("success",false);
            responseObject.put("message",e.getMessage());
        }
        return responseObject;
    }

    @Override
    public Map<String, Object> mergeChunks(String fileName) {
        Map<String,Object> responseObject = new HashMap<>();
        try{
            String filePath = "/mnt/chunk/"+fileName;
            String outputFilePath = "/mnt/chunk/"+fileName+"/"+fileName;
            File fileDirectory = new File(filePath);
            File[] chunkFiles = fileDirectory.listFiles();
            if (chunkFiles == null || chunkFiles.length == 0) {
                responseObject.put("success", false);
                responseObject.put("message", "No chunk files found");
                return responseObject;
            }
            try(OutputStream out = new FileOutputStream(outputFilePath)){
                for(long i=0;i<chunkFiles.length;i++){
                    File chunkFile = new File(filePath,fileName+"_chunkId_"+i);
                    try(InputStream in = new FileInputStream(chunkFile)){
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                }
            }

            responseObject.put("success",true);
            responseObject.put("filePath",outputFilePath);

        }
        catch (Exception e){
            responseObject.put("success",false);
            responseObject.put("message",e.getMessage());
        }
        return responseObject;
    }

}
