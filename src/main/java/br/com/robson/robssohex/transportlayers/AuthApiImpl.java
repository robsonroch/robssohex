package br.com.robson.robssohex.transportlayers;

import br.com.robson.robssohex.JwtUtil;
import br.com.robson.robssohex.UserClaims;
import br.com.robson.robssohex.api.AuthApi;
import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.model.LoginRequest;
import br.com.robson.robssohex.model.LoginResponse;
import br.com.robson.robssohex.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AuthApiImpl implements AuthApi {

    private final UserRepository userRepository;
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
}
