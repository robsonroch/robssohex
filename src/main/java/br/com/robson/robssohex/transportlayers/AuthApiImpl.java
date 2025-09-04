package br.com.robson.robssohex.transportlayers;

import br.com.robson.robssohex.JwtUtil;
import br.com.robson.robssohex.UserClaims;
import br.com.robson.robssohex.api.AuthApi;
import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.interactors.PreSignupService;
import br.com.robson.robssohex.model.*;
import br.com.robson.robssohex.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthApiImpl implements AuthApi {

    private final UserRepository userRepository;
    private final PreSignupService preSignupService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;
    private final UserClaimsMapper mapper;

    @Override
    public ResponseEntity<LoginResponse> authLoginPost(LoginRequest request) {
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());

        if (byEmail.isEmpty()) {
            // usuário não existe → 401 Unauthorized
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String criadanova = encoder.encode(request.getSenha());
        byEmail.ifPresent(u -> {
            System.out.println("Senha no banco: " + u.getSenha());
            System.out.println("Tentando validar com: " + request.getSenha());
            System.out.println("Match? " + encoder.matches(request.getSenha(), u.getSenha()));
            System.out.println("Decorder? " + encoder.upgradeEncoding(u.getSenha()));
        });
        User user = byEmail.get();

        // validar senha
        if (!encoder.matches(request.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        // gerar claims e token
        UserClaims claims = mapper.toClaims(user);
        String token = jwtUtil.generateToken(claims);

        // montar resposta com o token
        LoginResponse response = new LoginResponse().token(token);
        return ResponseEntity.ok(response);

    }

    @Override
    public ResponseEntity<PreSignupResponse> createPreSignup(PreSignupRequest preSignupRequest) {

        preSignupService.createPreSignup(preSignupRequest, "não-como pegar", "também não sei");

        var resp = new PreSignupResponse();
        resp.setMessage("E-mail enviado para continuação do cadastro.");
        return ResponseEntity.accepted().body(resp);
    }

    @Override
    public ResponseEntity<ValidateTokenResponse> validatePreSignupLink(UUID id, String token) {
        boolean valid = preSignupService.validate(id.toString(), token);
        if (!valid) {
            throw new IllegalStateException("Token inválido ou expirado");
        }
        // Gera JWT de pré-cadastro para permitir completar o cadastro
        String preSignupJwt = jwtUtil.generatePreSignupToken(id.toString(), 30); // 30 minutos de validade

        ValidateTokenResponse resp = new ValidateTokenResponse();
        resp.setId(id);
        resp.setToken(preSignupJwt);
        return ResponseEntity.ok(resp);
    }

    @Override
    public ResponseEntity<Void> completeSignup(CompleteSignupRequest completeSignupRequest) {
        preSignupService.complete(completeSignupRequest);
        return ResponseEntity.noContent().build();
    }


}
