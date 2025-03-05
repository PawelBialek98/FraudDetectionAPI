package com.example.model.entity;

import com.example.model.risk.RiskResult;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.types.ObjectId;

@Data
@EqualsAndHashCode(callSuper = true)
@MongoEntity(collection = "Transaction", database = "fraudDetection")
public class Transaction extends PanacheMongoEntity {

    private ObjectId id;
    private String binNumber;
    private long amount;
    private String currency;
    private String userLocation;
    private String requestId;
    private RiskResult riskResult;

    public Transaction() {
    }
}
