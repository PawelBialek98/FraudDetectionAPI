package com.example.utils.provider;

import io.quarkus.logging.Log;
import io.quarkus.runtime.util.StringUtil;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.MDC;

import java.util.UUID;

import static com.example.utils.Constants.REQUEST_ID_HEADER;

@Provider
public class RequestIdFilter implements ContainerRequestFilter {

    /**
     * Filter to fill incoming requests with X-REQUESTID if not provided
     *
     * @param requestContext Incoming request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String requestId = requestContext.getHeaderString(REQUEST_ID_HEADER);

        if (StringUtil.isNullOrEmpty(requestId)) {
            requestId = UUID.randomUUID().toString();
        }

        MDC.put(REQUEST_ID_HEADER, requestId);

        Log.info("Incoming request: " + requestContext.getMethod() + " " + requestContext.getUriInfo().getRequestUri());
    }
}
