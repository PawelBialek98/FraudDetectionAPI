package com.example.client;

import com.example.exception.MastercardBinLookupException;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.mastercardApi.MastercardException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.cache.CacheResult;
import io.quarkus.rest.client.reactive.ClientExceptionMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestHeader;

import java.util.List;

/**
 *  Represents Client API for Mastercard Bin Lookup
 */
@RegisterRestClient(baseUri = "https://sandbox.api.mastercard.com/bin-resources")
public interface MastercardBinClient {

    /**
     * Call Mastercard Bin Lookup /bin-ranges/account-searches endpoint to receive Bin related data
     *
     * @param authHeader - OAuth1.0a authorization header
     * @param requestId - X-REQUESTID for traceability
     * @param accountRange - Bin number to received data for
     * @return List of BinDetails with one BinDetail object
     * @throws MastercardBinLookupException - if Mastercard Api returns error 400 or above
     */
    @POST
    @Path("/bin-ranges/account-searches")
    @CacheResult(cacheName = "mastercard-bin-cache")
    List<BinDetails> getBinDetails(@RestHeader("Authorization") String authHeader,
                                   @RestHeader("X-REQUESTID") String requestId,
                                   String accountRange) throws MastercardBinLookupException;

    /**
     * Handler for errors with codes 400+
     *
     * @param response from MastercardApi
     * @return handled exception
     * @throws JsonProcessingException if MastercardApi throws not standardized error
     */
    @ClientExceptionMapper
    static RuntimeException toException(Response response) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        MastercardException errorResponse = mapper.readValue(response.readEntity(String.class), MastercardException.class);
        MastercardException.MastercardExceptionValues errorDetials = errorResponse.errors.error.getFirst();
        return new MastercardBinLookupException(errorDetials.reasonCode + " - "
                + errorDetials.description + " - "
                + errorDetials.details,
                response.getStatus());
    }
}