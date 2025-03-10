package com.example.service;

import com.example.client.MastercardBinClient;
import com.example.exception.SigningKeyException;
import com.example.logging.Logged;
import com.example.model.mastercardApi.BinDetails;
import com.mastercard.developer.oauth.OAuth;
import com.mastercard.developer.utils.AuthenticationUtils;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import static com.example.utils.Constants.REQUEST_ID_HEADER;

@Logged
@ApplicationScoped
public class MastercardBinService {

    @Inject
    @RestClient
    MastercardBinClient mastercardBinClient;

    @ConfigProperty(name = "mastercard.api.consumerKey")
    String consumerKey;

    @ConfigProperty(name = "mastercard.api.pkcs12Key.filePath")
    String pkcs12KeyFilePath;

    @ConfigProperty(name = "mastercard.api.signingKey.alias")
    String signingKeyAlias;

    @ConfigProperty(name = "mastercard.api.signingKey.password")
    String signingKeyPassword;

    @ConfigProperty(name = "quarkus.rest-client.mastercard-bin-client.url")
    String mastercardBinClientUrl;

    private PrivateKey signingKey;

    public BinDetails getBinDetails(String binId) {

        String payload = "{\"accountRange\":\"" + binId + "\"}";
        String authHeader = prepareAuthHeader(payload);

        String requestId = MDC.get(REQUEST_ID_HEADER);
        List<BinDetails> binDetails = mastercardBinClient.getBinDetails(authHeader, requestId, payload);
        return binDetails.stream().findFirst().orElseThrow(NotFoundException::new);
    }

    public String prepareAuthHeader(String payload) {
        if (signingKey == null) {
            loadSigningKey();
        }

        URI uri = URI.create(mastercardBinClientUrl + "/bin-ranges/account-searches");
        String method = "POST";
        Charset charset = StandardCharsets.UTF_8;
        return OAuth.getAuthorizationHeader(uri, method, payload, charset, consumerKey, signingKey);
    }

    private void loadSigningKey() {
        try {
            this.signingKey = AuthenticationUtils.loadSigningKey(pkcs12KeyFilePath,
                    signingKeyAlias,
                    signingKeyPassword);
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                 UnrecoverableKeyException e) {
            Log.error(e);
            throw new SigningKeyException("Problem while signing key" + e);
        }
    }
}
