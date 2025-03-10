package com.example.client;

import com.example.exception.MastercardBinLookupException;
import com.example.model.mastercardApi.BinDetails;
import com.example.service.MastercardBinService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@QuarkusTest
public class MastercardBinClientTest {

    @Inject
    MastercardBinService mastercardBinService;

    @Inject
    @RestClient
    MastercardBinClient mastercardBinClient;

    private final String binId = "99875393";
    private final String requestId = UUID.randomUUID().toString();
    private final String accountRange = "{\"accountRange\":\"" + binId + "\"}";

    @Test
    public void testGetBinDetailsSuccess() {
        List<BinDetails> response = mastercardBinClient.getBinDetails(mastercardBinService.prepareAuthHeader(accountRange), requestId, accountRange);
        assertNotNull(response);
        assertNotNull(response.getFirst());
        assertEquals(binId.substring(0, response.getFirst().binLength()), response.getFirst().binNum());
        assertEquals("VILLAGE BANK", response.getFirst().customerName());
    }

    @Test
    void testGetBinDetailsThrowsException() throws MastercardBinLookupException {
        MastercardBinLookupException exception = assertThrows(MastercardBinLookupException.class,
                () -> mastercardBinClient.getBinDetails("invalidHeader", requestId, accountRange));

        assertTrue(exception.getMessage().contains("MALFORMED_OAUTH_REQ"));
    }

    @Test
    void testGetBinDetailsRealApiThrowsException() {
        String invalidAccountRange = "000000";
        String authHeader = mastercardBinService.prepareAuthHeader(invalidAccountRange);

        MastercardBinLookupException exception = assertThrows(MastercardBinLookupException.class,
                () -> mastercardBinClient.getBinDetails(authHeader, requestId, invalidAccountRange)
        );

        assertTrue(exception.getMessage().contains("invalid.request"));
    }
}
