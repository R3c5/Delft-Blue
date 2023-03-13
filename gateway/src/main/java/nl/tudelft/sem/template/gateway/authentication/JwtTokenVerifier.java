package nl.tudelft.sem.template.gateway.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Verifies the JWT token in the request for validity.
 */
@Component
public class JwtTokenVerifier {
    @Value("${jwt.secret}")  // automatically loads jwt.secret from resources/application.properties
    private transient String jwtSecret;

    /**
     * Validate the JWT token for expiration.
     */
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String getNetIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getFaculties(String token) {
        return getClaims(token).get("FACULTIES", String.class);
    }

    public String getRole(String token) {
        System.out.println(getClaims(token));
        return getClaims(token).get("ROLE", String.class);
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}
