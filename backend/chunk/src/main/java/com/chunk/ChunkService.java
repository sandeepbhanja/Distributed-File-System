package com.chunk;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
public interface ChunkService {

    Map<String,Object> storeChunk(String fileName, String chunkId, InputStream chunkStream);
    Map<String,Object> mergeChunks(String fileName);

}
