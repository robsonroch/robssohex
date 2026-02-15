package br.com.robson.robssohex.transportlayers.filters;

import br.com.robson.robssohex.CachedBodyHttpServletRequest;
import br.com.robson.robssohex.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class PasswordResetJwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public PasswordResetJwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().equals("/robssohex/auth/password-reset/complete");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

        String authHeader = cachedRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String token = authHeader.substring(7);
        try {
            var claims = jwtUtil.getClaims(token);
            if (!"password-reset".equals(claims.get("type"))) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            String requestId = claims.getSubject();
            String idFromBody = extractIdFromRequestBody(cachedRequest);
            if (idFromBody == null || !requestId.equals(idFromBody)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        } catch (JwtException | IllegalArgumentException e) {
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