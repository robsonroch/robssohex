package br.com.robson.robssohex.transportlayers.filters;

import br.com.robson.robssohex.CachedBodyHttpServletRequest;
import br.com.robson.robssohex.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class PasswordChangeJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private static final Logger log = LoggerFactory.getLogger(PasswordChangeJwtFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !("POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().equals("/robssohex/auth/password-change/complete"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

        String authHeader = cachedRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("PasswordChangeJwtFilter: Authorization ausente ou inválido");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = authHeader.substring(7);
        try {
            var claims = jwtUtil.getClaims(token);
            if (!"change-password".equals(claims.get("type"))) {
                log.debug("PasswordChangeJwtFilter: tipo de token inválido");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String subject = claims.getSubject();
            String claimId = claims.get("id", String.class);
            String requestId = subject != null ? subject : claimId;
            if (requestId == null) {
                log.debug("PasswordChangeJwtFilter: token sem subject/id");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            if (claimId != null && !claimId.equals(requestId)) {
                log.debug("PasswordChangeJwtFilter: subject/id inconsistente");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String idFromBody = extractIdFromRequestBody(cachedRequest);
            if (idFromBody == null || !requestId.equals(idFromBody)) {
                log.debug("PasswordChangeJwtFilter: id do body não confere");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("PasswordChangeJwtFilter: JWT inválido", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(cachedRequest, response);
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
}