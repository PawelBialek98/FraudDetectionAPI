package com.example.service;

import com.example.client.MastercardBinClient;
import com.example.exception.SigningKeyException;
import com.example.model.mastercardApi.BinDetails;
import com.mastercard.developer.oauth.OAuth;
import com.mastercard.developer.utils.AuthenticationUtils;
import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

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

    @CacheResult(cacheName = "bin-cache")
    public BinDetails getBinDetails(String binId, String requestId) {

        String payload = "{\"accountRange\":\"" + binId + "\"}";
        String authHeader = prepareAuthHeader(payload);

        List<BinDetails> binDetails = mastercardBinClient.getBinDetails(authHeader, requestId, payload);
        return binDetails.stream().findFirst().orElseThrow(NotFoundException::new);
    }

    public String prepareAuthHeader(String payload){
        PrivateKey signingKey = null;
        try {
            signingKey = AuthenticationUtils.loadSigningKey(pkcs12KeyFilePath,
                    signingKeyAlias,
                    signingKeyPassword);
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                 UnrecoverableKeyException e) {
            Log.error(e);
            throw new SigningKeyException("Problem while signing key" + e);
        }

        URI uri = URI.create("https://sandbox.api.mastercard.com/bin-resources/bin-ranges/account-searches");
        String method = "POST";
        Charset charset = StandardCharsets.UTF_8;
        return OAuth.getAuthorizationHeader(uri, method, payload, charset, consumerKey, signingKey);
    }
}
