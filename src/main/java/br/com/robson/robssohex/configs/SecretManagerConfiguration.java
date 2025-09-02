package br.com.robson.robssohex.configs;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Base64;

@Configuration
public class SecretManagerConfiguration {

    private static final String LOCALSTACK_ENDPOINT = "http://localhost:4566";
    private static final Region REGION = Region.US_EAST_1;

    public <T> T getSecretProperties(String secretKey, Class<T> clazz) throws IOException {
        return getSecret(secretKey, clazz);
    }

    private <T> T getSecret(String secretId, Class<T> clazz) throws IOException {
        SecretsManagerClient client = SecretsManagerClient.builder()
                .endpointOverride(URI.create(LOCALSTACK_ENDPOINT))
                .region(REGION)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
                )
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(secretId).build();
        GetSecretValueResponse getSecretValueResult = client.getSecretValue(getSecretValueRequest);

        String secretString = null;
        if (getSecretValueResult.secretString() != null) {
            secretString = getSecretValueResult.secretString();
        } else {
            secretString = new String(Base64.getDecoder().decode(getSecretValueResult.secretBinary().asByteArray()));
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        return mapper.readValue(secretString, clazz);
    }
}
