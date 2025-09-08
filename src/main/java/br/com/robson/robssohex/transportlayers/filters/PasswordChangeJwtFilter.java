package br.com.robson.robssohex.transportlayers.filters;

import br.com.robson.robssohex.CachedBodyHttpServletRequest;
import br.com.robson.robssohex.JwtUtil;
import br.com.robson.robssohex.entities.User;
import br.com.robson.robssohex.repositories.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class PasswordChangeJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/robssohex/auth/password-change-request") && !request.getRequestURI().equals("/robssohex/auth/password-change/complete");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {

                if(request.getRequestURI().equals("/robssohex/auth/password-change/complete")){
                    String id = jwtUtil.extractId(token);
                    autenticarUsuarioComId(id);
                }else{
                    String id = jwtUtil.extractEmail(token);
                    autenticarComEmail(id);
                }

            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractIdFromRequestBody(HttpServletRequest request) {
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            var reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"").matcher(body);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void autenticarComEmail(String email) {
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
        authentication.setDetails(user); // Guarda o ID real para uso futuro

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void autenticarUsuarioComId(String id) {
        User user = userRepository.findById(id).orElse(null);
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
        authentication.setDetails(user); // Guarda o ID real para uso futuro

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}