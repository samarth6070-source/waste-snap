package com.wastemanagement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class FirebaseAuthService {

    @Value("${app.firebase.api-key:}")
    private String firebaseApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public AuthenticatedUser verifyIdToken(String idToken) throws FirebaseAuthException, IOException, InterruptedException {
        if (!FirebaseApp.getApps().isEmpty()) {
            FirebaseToken token = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return new AuthenticatedUser(token.getUid(), token.getEmail(), token.getName());
        }
        return verifyWithIdentityToolkit(idToken);
    }

    private AuthenticatedUser verifyWithIdentityToolkit(String idToken) throws IOException, InterruptedException {
        if (!StringUtils.hasText(firebaseApiKey)) {
            throw new IllegalStateException("Firebase API key is missing.");
        }

        String endpoint = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" + firebaseApiKey;
        String requestBody = "{\"idToken\":\"" + idToken + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("Identity lookup failed.");
        }

        JsonNode root = objectMapper.readTree(response.body());
        JsonNode users = root.path("users");
        if (!users.isArray() || users.isEmpty()) {
            throw new IllegalStateException("User not found for token.");
        }

        JsonNode user = users.get(0);
        return new AuthenticatedUser(
                user.path("localId").asText(null),
                user.path("email").asText(null),
                user.path("displayName").asText("")
        );
    }
}
