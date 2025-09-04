package br.com.robson.robssohex;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "x84Lq@vN%z7yUt3s#G9PwmA!Kd5TqR1$"; // ≥ 32 chars
    private static final byte[] SECRET_KEY = SECRET.getBytes(StandardCharsets.UTF_8);

    public String generateToken(UserClaims user) {

        String compact = Jwts.builder()
                .setSubject(user.getUsername()) // usado como principal
                .claim("User", user)
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(2, ChronoUnit.HOURS)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
        return compact;
    }

    public String generatePreSignupToken(String preSignupId, int minutes) {
        return Jwts.builder()
                .setSubject(preSignupId)
                .claim("type", "pre-signup")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(minutes * 60L)))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
