package com.example.resource;

import com.example.model.mastercardApi.BinDetails;
import com.example.service.MastercardBinService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TransactionResourceTest {

    private final static String BIN_DETAILS_PATH = "/getBinDetails";

    @Inject
    TransactionResource transactionResource;

    @InjectMock
    MastercardBinService mastercardBinService;

    @Test
    public void testValidBinNumber() {
        String validBin = "99875393";
        BinDetails binDetails = new BinDetails();
        binDetails.setBinNum(validBin);

        when(mastercardBinService.getBinDetails(validBin, UUID.randomUUID().toString())).thenReturn(Optional.of(binDetails));

        given()
                .contentType(ContentType.JSON)
                .queryParam("binNumber", validBin)  // Przekazujemy binNumber jako parametr zapytania
                .when()
                .get(BIN_DETAILS_PATH)  // Wywołujemy endpoint
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void testEmptyBinNumber() {
        String emptyBin = "";

        given()
                .contentType(ContentType.JSON)
                .queryParam("binNumber", emptyBin)  // Przekazujemy pusty numer BIN
                .when()
                .get(BIN_DETAILS_PATH)  // Wywołujemy endpoint
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)  // Oczekujemy kodu odpowiedzi 400
                .body("message", is("Bin may not be blank"));  // Sprawdzamy komunikat o błędzie
    }

    @Test
    public void testInvalidBinNumberPattern() {
        String invalidBin = "12345";

        given()
                .contentType(ContentType.JSON)
                .queryParam("binNumber", invalidBin)  // Przekazujemy nieprawidłowy numer BIN
                .when()
                .get(BIN_DETAILS_PATH)  // Wywołujemy endpoint
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)  // Oczekujemy kodu odpowiedzi 400
                .body("message", is("Bin should contains only 8 digits"));  // Sprawdzamy komunikat o błędzie
    }

    @Test
    public void testBinNotFound() {
        String nonExistingBin = "00000000";
        when(mastercardBinService.getBinDetails(nonExistingBin, UUID.randomUUID().toString())).thenReturn(Optional.empty());


        given()
                .contentType(ContentType.JSON)
                .queryParam("binNumber", nonExistingBin)
                .when()
                .get(BIN_DETAILS_PATH)
                .then()
                .statusCode(HttpStatus.SC_NOT_FOUND);
    }
}
