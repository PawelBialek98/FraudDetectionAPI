package com.example.resource;

import com.example.GenerateToken;
import com.example.exception.MastercardBinLookupException;
import com.example.model.api.TransactionAPI;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.risk.RiskResult;
import com.example.service.MastercardBinService;
import com.example.service.RiskAssessmentService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.Header;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import static com.example.utils.Constants.REQUEST_ID_HEADER;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
@TestHTTPEndpoint(FraudDetectionResource.class)
public class FraudDetectionResourceTest {

    @TestHTTPResource("/getBinDetails")
    URL getBinDetails;

    @TestHTTPResource("evaluateTransaction")
    URL evaluateTransaction;

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

    @Test
    void testGetBinDetails_Success() {
        String binNum = "12345678";

        BinDetails mockBinDetails = BinDetails.builder()
                .binNum(binNum)
                .governmentRange(true)
                .customerName("VILLAGE BANK")
                .build();

        when(mastercardBinService.getBinDetails(anyString()))
                .thenReturn(mockBinDetails);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(getBinDetails)
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
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(getBinDetails)
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
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(getBinDetails)
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
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .queryParam("binNumber", binNum)
                .when()
                .get(getBinDetails)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("violations.message", hasItem("Bin should contain only 8 digits"));
    }

    @Test
    void testEvaluateTransaction_Success() {
        TransactionAPI request = new TransactionAPI("12345678", 10000, "USD", "USA");
        RiskResult response = new RiskResult(85, List.of("High risk"));

        when(riskAssessmentService.assessRisk(any(TransactionAPI.class)))
                .thenReturn(response);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .body(request)
                .when()
                .post(evaluateTransaction)
                .then()
                .statusCode(Response.Status.OK.getStatusCode())
                .body("totalRiskScore", equalTo(85))
                .body("reasons", hasItem("High risk"));
    }

    @Test
    void testEvaluateTransaction_Unauthorized() {
        TransactionAPI request = new TransactionAPI("12345678", 10000, "USD", "USA");

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID)) // No auth header
                .body(request)
                .when()
                .post(evaluateTransaction)
                .then()
                .statusCode(Response.Status.UNAUTHORIZED.getStatusCode());
    }

    @Test
    void testEvaluateTransaction_InvalidRequest() {
        TransactionAPI request = new TransactionAPI("", -5000, "XYZ", "AAA"); // Invalid data

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .body(request)
                .when()
                .post(evaluateTransaction)
                .then()
                .statusCode(Response.Status.BAD_REQUEST.getStatusCode())
                .body("violations.size()", greaterThan(0));
    }

    @Test
    void testEvaluateTransaction_InternalServerError() {
        TransactionAPI request = new TransactionAPI("12345678", 10000, "USD", "USA");

        when(riskAssessmentService.assessRisk(any(TransactionAPI.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .header(new Header("Authorization", authHeader))
                .header(new Header(REQUEST_ID_HEADER, REQUEST_ID))
                .body(request)
                .when()
                .post(evaluateTransaction)
                .then()
                .statusCode(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                .body("details", containsString("java.lang.RuntimeException: Unexpected error"));
    }
}
