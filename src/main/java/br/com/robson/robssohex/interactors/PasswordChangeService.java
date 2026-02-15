package br.com.robson.robssohex.interactors;

import br.com.robson.robssohex.datasources.EmailServiceAdapter;
import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.model.CompletePasswordChangeRequest;
import br.com.robson.robssohex.model.CompleteSignupRequest;
import br.com.robson.robssohex.repositories.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordChangeService {

    private final EmailServiceAdapter emailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final RedisTemplate<String, Object> redisTemplate;
    private final String TOKEN_CACHE_KEY = "passwordChangeTokens";
    private final String continueBaseUrl = "http://localhost:5173/password-change";
    private static final int RATE_LIMIT_MAX = 5;
    private static final Duration RATE_LIMIT_WINDOW = Duration.ofHours(1);

    // tokenId -> (tokenHash, expiresAt)
    private final Map<String, TokenData> tokenCache = new ConcurrentHashMap<>();

    @Transactional
    public void createPasswordChangeRequest() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        Object details = authentication.getDetails();
        String email = details instanceof String ? (String) details : null;
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("E-mail do usuário não encontrado no contexto");
        }

        User userFromBase = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        enforceRateLimit("password-change:user:" + userFromBase.getId());

        String rawToken = generateRawToken();
        String tokenHash = BCrypt.hashpw(rawToken, BCrypt.gensalt());
        Instant expires = Instant.now().plusSeconds(24 * 3600);
        UUID tokenId = UUID.randomUUID();

        redisTemplate.opsForHash().put(TOKEN_CACHE_KEY, tokenId.toString(), new TokenData(tokenHash, expires, userFromBase.getId()));


        String link = buildContinueLink(tokenId, rawToken);

        var emailRequest = new EmailRequest();
        emailRequest.setDestino(userFromBase.getEmail());
        emailRequest.setNomeUsuario(userFromBase.getUsername());
        emailRequest.setSistemaOrigem("SSO");
        emailRequest.setConteudoHtml(List.of(new EmailHtmlElemento("linkAlteracaoSenha", link)));

        emailSender.enviarEmail(emailRequest);
    }

    public String buildContinueLink(UUID id, String rawToken) {
        return UriComponentsBuilder
                .fromUriString(continueBaseUrl)
                .queryParam("id", id)
                .queryParam("token", rawToken)
                .build()
                .toUriString();
    }

    public boolean validate(String id, String token) {

        TokenData data = (TokenData) redisTemplate.opsForHash().get(TOKEN_CACHE_KEY, id);

        if (data == null || data.expiresAt.isBefore(Instant.now())) {
            redisTemplate.opsForHash().delete(TOKEN_CACHE_KEY, id);
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        boolean valid = BCrypt.checkpw(token, data.tokenHash);
        if (!valid) {
            throw new IllegalArgumentException("Token inválido");
        }
        return true;
    }

    @Transactional
    public void complete(CompletePasswordChangeRequest req) {
        TokenData data = (TokenData) redisTemplate.opsForHash().get(TOKEN_CACHE_KEY, req.getId());
        if (data == null || data.expiresAt.isBefore(Instant.now())) {
            redisTemplate.opsForHash().delete(TOKEN_CACHE_KEY, req.getId());
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        User user = userRepository.findById(data.userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        user.setSenha(encoder.encode(req.getNovaSenha()));
        userRepository.save(user);
        redisTemplate.opsForHash().delete(TOKEN_CACHE_KEY, req.getId()); // single-use
    }

    private void enforceRateLimit(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redisTemplate.expire(key, RATE_LIMIT_WINDOW);
        }
        if (count != null && count > RATE_LIMIT_MAX) {
            throw new IllegalStateException("Muitas solicitações. Tente novamente mais tarde.");
        }
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Getter
    private static class TokenData implements Serializable{
        final String tokenHash;
        final Instant expiresAt;
        final String userId;

        TokenData(String tokenHash, Instant expiresAt, String userId) {
            this.tokenHash = tokenHash;
            this.expiresAt = expiresAt;
            this.userId = userId;
        }
    }
}
