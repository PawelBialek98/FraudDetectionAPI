package com.example.resource;

import com.example.exception.MastercardBinLookupException;
import com.example.exception.SigningKeyException;
import com.example.model.api.SimpleBinAPI;
import com.example.model.api.TransactionAPI;
import com.example.service.MastercardBinService;
import com.example.service.RiskAssessmentService;
import io.quarkus.logging.Log;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.UUID;
import java.util.function.Supplier;

import static com.example.utils.Constants.REQUEST_ID_HEADER;

@Path("")
@RequestScoped
public class FraudDetectionResource {

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
    public Response evaluateTransaction(@Valid TransactionAPI input) {
        return handleExceptions(() -> riskAssessmentService.assessRisk(input));
    }

    @GET
    @Path("/getBinDetails")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"User"})
    @Operation(summary = "Bin Details", description = "Receive Bin number details from mastercard API")
    public Response getBinDetails(@Valid @BeanParam SimpleBinAPI request) {
        return handleExceptions(() -> mastercardBinService.getBinDetails(request.getBinNumber()));
    }

    private <T> Response handleExceptions(Supplier<T> action) {
        try {
            T result = action.get();
            return Response.ok(result).build();
        } catch (MastercardBinLookupException e) {
            Log.error(e);
            return Response.status(Response.Status.fromStatusCode(e.getStatusCode())).entity(e.getMessage()).build();
        } catch (SigningKeyException e) {
            Log.error(e);
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
