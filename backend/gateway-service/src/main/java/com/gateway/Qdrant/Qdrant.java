package com.gateway.Qdrant;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.grpc.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Qdrant {


    private final QdrantClient client;

    public Qdrant(@Value("${qdrant_api_key}") String qdrantAPIKey, @Value("${qdrant_endpoint}") String qdrantEndPoint) {
         this.client =
                new QdrantClient(
                        QdrantGrpcClient.newBuilder(
                                        qdrantEndPoint,
                                        6334,
                                        true)
                                .withApiKey(qdrantAPIKey)
                                .build());
         createCollection("test_collection");
    }

    public void createCollection(String collectionName){
        this.client.createCollectionAsync("test_collection", Collections.VectorParams.newBuilder().setDistance(Collections.Distance.Cosine).setSize(300).build());
    }

}
