package com.example.resource;

import com.example.exception.MastercardBinLookupException;
import com.example.exception.SigningKeyException;
import com.example.model.mastercardApi.BinDetails;
import com.example.model.api.SimpleBinAPI;
import com.example.model.api.TransactionAPI;
import com.example.service.MastercardBinService;
import com.example.service.RiskAssessmentService;
import com.example.model.risk.RiskResult;
import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.Optional;
import java.util.UUID;

import static com.example.utils.Constants.REQUEST_ID_HEADER;

@Path("")
@RequestScoped
public class TransactionResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    MastercardBinService mastercardBinService;

    @Inject
    RiskAssessmentService riskAssessmentService;

    @POST
    @Path("/evaluateTransaction")
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Evaluate Transaction", description = "This metod evaluate transaction and returns it score")
    public Response evaluateTransaction(@Valid TransactionAPI input, @Context HttpHeaders headers) {
        String requestId = getRequestIdHeader(headers);
        RiskResult riskResult;
        try{
            riskResult = riskAssessmentService.assessRisk(input, requestId);
        } catch (MastercardBinLookupException e){
            Log.error(e);
            return Response.status(Response.Status.fromStatusCode(e.getStatusCode())).entity(e.getMessage()).build();
        } catch (SigningKeyException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
        return Response.ok(riskResult).build();
    }

    @GET
    @Path("/getBinDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({ "User", "Admin" })
    @Operation(summary = "Bin Details", description = "Receive Bin number details from mastercard API")
    public Response getBinDetails(@Valid @BeanParam SimpleBinAPI request, @Context HttpHeaders headers) {
        String requestId = getRequestIdHeader(headers);
        Optional<BinDetails> binDetailsOptional;
        try{
            binDetailsOptional = mastercardBinService.getBinDetails(request.getBinNumber(), requestId);
        } catch (MastercardBinLookupException e){
            Log.error(e);
            return Response.status(Response.Status.fromStatusCode(e.getStatusCode())).entity(e.getMessage()).build();
        } catch (SigningKeyException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        if (binDetailsOptional.isPresent()) {
            return Response.ok().entity(binDetailsOptional.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Bin not found").build();
        }
    }

    private String getRequestIdHeader(HttpHeaders headers) {
        String requestId = headers.getHeaderString(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }
}
