package br.com.robson.robssohex.interactors;

import br.com.robson.robssohex.datasources.EmailServiceAdapter;
import br.com.robson.robssohex.entities.PreRegistration;
import br.com.robson.robssohex.model.CompleteSignupRequest;
import br.com.robson.robssohex.model.PreSignupRequest;
import br.com.robson.robssohex.repositories.PreRegistrationRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class PreSignupService {

    private final PreRegistrationRepository repo;
    private final EmailServiceAdapter emailSender;
    private final UserService userService; // seu serviço que cria o usuário “definitivo”
    private String continueBaseUrl = "http://localhost:5173/complete-signup";  // ex: https://app.seudominio.com/complete-signup

    private final SecureRandom secureRandom = new SecureRandom();

    public PreSignupService(PreRegistrationRepository repo,
                            EmailServiceAdapter emailSender,
                            UserService userService
                            ) {
        this.repo = repo;
        this.emailSender = emailSender;
        this.userService = userService;
    }

    @Transactional
    public void createPreSignup(PreSignupRequest req, String ip, String ua) {
        String rawToken = generateRawToken();
        String tokenHash = BCrypt.hashpw(rawToken, BCrypt.gensalt());
        Instant expires = Instant.now().plusSeconds(24 * 3600);

        var pre = new PreRegistration();
        pre.setUsername(req.getUsername().trim());
        pre.setEmail(req.getEmail().trim().toLowerCase());
        pre.setTokenHash(tokenHash);
        pre.setExpiresAt(expires);
        pre.setCreatedIp(ip);
        pre.setCreatedUserAgent(ua);

        repo.save(pre);

        String link = buildContinueLink(pre.getId(), rawToken);

        // Criação do EmailRequest
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setDestino(pre.getEmail());
        emailRequest.setNomeUsuario(pre.getUsername());
        emailRequest.setSistemaOrigem("SSO"); // ou qualquer nome do sistema de origem

        // Conteúdo HTML do e-mail
        var conteudoHtml = List.of(new EmailHtmlElemento("linkCadastro", link));
        emailRequest.setConteudoHtml(conteudoHtml);

        // Envio de e-mail via o adaptador
        emailSender.enviarEmail(emailRequest);
    }

    public String buildContinueLink(UUID id, String rawToken) {
        return UriComponentsBuilder
                .fromUriString(continueBaseUrl) // front-end route
                .queryParam("id", id)
                .queryParam("token", rawToken)
                .build()
                .toUriString();
    }

    public boolean validate(String id, String token) {
        var opt = repo.findByIdAndUsedAtIsNullAndExpiresAtAfter(
                UUID.fromString(id), Instant.now()
        );
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Link inválido ou expirado");
        }
        return BCrypt.checkpw(token, opt.get().getTokenHash());
    }

    @Transactional
    public void complete(CompleteSignupRequest req) {
        var id = UUID.fromString(req.getId().toString());
        var pre = repo.findByIdAndUsedAtIsNullAndExpiresAtAfter(id, Instant.now())
                .orElseThrow(() -> new IllegalArgumentException("Link inválido ou expirado"));

        // Cria o usuário definitivo
        LocalDate dob = req.getDataNascimento();
        userService.createUserFromPreSignup(pre.getUsername(), pre.getEmail(), req.getSenha(), dob);

        // Inutiliza o pré-cadastro (single-use)
        pre.setUsedAt(Instant.now());
        repo.save(pre);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32]; // 256 bits
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
