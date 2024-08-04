
package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class CreateParkingLots implements RequestHandler<Map<String, Object>, String> {

    private static final DynamoDbClient dynamoDbClient = DynamoDbClient.builder().build();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        try {
            saveItem(
                    input.get("id").toString(),
                    (Map<String, Double>) input.get("location"),
                    (int) input.get("parkingSpacesInUse")
            );
            return "Item inserted successfully!";
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            return "Failed to insert item.";
        }
    }

    public static void saveItem(String id, Map<String, Double> location, int parkingSpacesInUse) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(id).build());
        item.put("location", AttributeValue.builder().m(Map.of(
                "lat", AttributeValue.builder().n(location.get("lat").toString()).build(),
                "lon", AttributeValue.builder().n(location.get("lon").toString()).build()
        )).build());
        item.put("parkingSpacesInUse", AttributeValue.builder().n(String.valueOf(parkingSpacesInUse)).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("parking-lots")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }
}