package com.example.resource;

import com.example.GenerateToken;
import com.example.exception.MastercardBinLookupException;
import com.example.model.mastercardApi.BinDetails;
import com.example.service.MastercardBinService;
import com.example.service.RiskAssessmentService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.UUID;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class FraudDetectionResourceTest {

    private final static String GET_BIN_DETAILS = "/getBinDetails";

    @InjectMock
    MastercardBinService mastercardBinService;

    @InjectMock
    RiskAssessmentService riskAssessmentService;

    private static final String REQUEST_ID = UUID.randomUUID().toString();

    private static String authHeader;

    @BeforeAll
    public static void setup() {
        authHeader = "Bearer " + GenerateToken.generateToken();
    }

//    @Test
//    void testEvaluateTransaction_Success() {
//        TransactionAPI transaction = new TransactionAPI(); // Utwórz obiekt testowy
//        RiskResult mockRiskResult = new RiskResult("LOW_RISK");
//
//        when(riskAssessmentService.assessRisk(any(TransactionAPI.class), anyString()))
//                .thenReturn(mockRiskResult);
//
//        given()
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(new Header("Authorization", "Bearer test-token"))
//                .header(new Header("X-Request-ID", REQUEST_ID))
//                .body(transaction)
//                .when()
//                .post("/evaluateTransaction")
//                .then()
//                .statusCode(Response.Status.OK.getStatusCode())
//                .body("riskLevel", equalTo("LOW_RISK"));
//
//        verify(riskAssessmentService, times(1)).assessRisk(any(TransactionAPI.class), anyString());
//    }
//
//    @Test
//    void testEvaluateTransaction_MastercardBinLookupException() {
//        TransactionAPI transaction = new TransactionAPI();
//
//        when(riskAssessmentService.assessRisk(any(TransactionAPI.class), anyString()))
//                .thenThrow(new MastercardBinLookupException("BIN Lookup Failed", 400));
//
//        given()
//                .contentType(MediaType.APPLICATION_JSON)
//                .header(new Header("Authorization", "Bearer test-token"))
//                .header(new Header("X-Request-ID", REQUEST_ID))
//                .body(transaction)
//                .when()
//                .post("/evaluateTransaction")
//                .then()
//                .statusCode(400)
//                .body(equalTo("BIN Lookup Failed"));
//    }

    @Test
    void testGetBinDetails_Success() {
        String binNum = "12345678";

        BinDetails mockBinDetails = new BinDetails(); //"123456", "Mastercard", "Bank XYZ"
        mockBinDetails.setBinNum("12345678");
        mockBinDetails.setGovernmentRange(true);
        mockBinDetails.setCustomerName("VILLAGE BANK");

        when(mastercardBinService.getBinDetails(anyString()))
                .thenReturn(mockBinDetails);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header("X-Request-ID", REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(GET_BIN_DETAILS)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("binNum", equalTo(binNum))
                .body("governmentRange", equalTo(true))
                .body("customerName", equalTo("VILLAGE BANK"));

        verify(mastercardBinService, times(1)).getBinDetails(eq(binNum));
    }

    @Test
    void testGetBinDetails_NotFound() {
        String binNum = "00000000";

        when(mastercardBinService.getBinDetails(eq(binNum)))
                .thenThrow(new NotFoundException());

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header("X-Request-ID", REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(GET_BIN_DETAILS)
                .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    void testGetBinDetails_MastercardApiError() {
        String binNum = "00000000";

        when(mastercardBinService.getBinDetails(eq(binNum)))
                .thenThrow(new MastercardBinLookupException("some_message", 401));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header("X-Request-ID", REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(GET_BIN_DETAILS)
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode())
                .body(equalTo("some_message"));
    }

    @Test
    void testGetBinDetails_FailedBinNumValidation() {
        String binNum = "1234";

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header("X-Request-ID", REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(GET_BIN_DETAILS)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("violations.message", hasItem("Bin should contain only 8 digits"));
    }
}
