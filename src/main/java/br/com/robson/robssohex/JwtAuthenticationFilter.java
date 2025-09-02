package br.com.robson.robssohex;

import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/sso/auth/login")
                || path.equals("/sso/openapi.yaml")
                || path.startsWith("/sso/swagger-ui")
                || path.startsWith("/sso/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = jwtUtil.extractEmail(token);
                autenticarUsuario(email);
            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarUsuario(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return;

        // Permissões das roles
        List<String> roleAuthorities = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName().toUpperCase()) // ex: ROLE_ADMIN
                .toList();

        List<String> permissionAuthorities = Stream.concat(
                        user.getRoles().stream()
                                .flatMap(role -> role.getPermissions().stream()),
                        user.getPermissions().stream()
                )
                .map(p -> p.getAction() + ":" + p.getResource()) // ex: "create:user"
                .toList();

        List<SimpleGrantedAuthority> authorities = Stream.concat(
                        roleAuthorities.stream(),
                        permissionAuthorities.stream()
                )
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        var authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
        authentication.setDetails(user.getEmail()); // Guarda o ID real para uso futuro

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
