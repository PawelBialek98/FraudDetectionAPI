package com.example.model.entity;

import com.example.model.risk.RiskResult;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Builder;
import org.bson.types.ObjectId;

@Builder
@MongoEntity(collection = "Transaction")
public record Transaction(
        ObjectId id,
        String binNumber,
        long amount,
        String currency,
        String userLocation,
        String requestId,
        RiskResult riskResult
) {
}
