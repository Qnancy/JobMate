package cn.edu.zju.cs.jobmate.security.jwt;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import cn.edu.zju.cs.jobmate.configs.properties.JwtProperties;
import cn.edu.zju.cs.jobmate.models.User;

/**
 * JWT Token Provider.
 */
@Component
public class JwtTokenProvider {

    @Autowired
    private JwtProperties properties;

    /**
     * Generate JWT token for a given username.
     * 
     * @param user the {@link User} entity
     * @return the generated JWT token
     * @throws JOSEException if signing fails
     */
    public String generateToken(User user) throws JOSEException {
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getExpiration());

        // Prepare the JWT.
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(UUID.randomUUID().toString())
            .subject(user.getUsername())
            .issueTime(now)
            .issuer("JobMate")
            .expirationTime(exp)
            // TODO: add fake id.
            .claim("roles", user.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .toList())
            .build();

        // Sign the JWT.
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        JWSSigner signer = new MACSigner(properties.getSecret().getBytes());
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    /**
     * Validate the JWT token.
     * 
     * @param token the JWT token
     * @return true if valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(properties.getSecret().getBytes());
            // Verify the signature.
            if (!signedJWT.verify(verifier)) {
                return false;
            }
            // Verify the expiration time.
            Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
            return exp != null && exp.after(new Date());
        } catch (JOSEException | ParseException e) {
            return false;
        }
    }

    /**
     * Get username from JWT token.
     * 
     * @param token the JWT token
     * @return the extracted username
     * @throws ParseException if parsing fails
     */
    public String getUsernameFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }

    /**
     * Get remaining time to expiry in millisecond from JWT token.
     * 
     * @param token the JWT token
     * @return the remaining time to expiry in milliseconds
     * @throws ParseException if parsing fails
     */
    public long getRemainTimeFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date exp = signedJWT.getJWTClaimsSet().getExpirationTime();
        return exp.getTime() - System.currentTimeMillis();
    }

    /**
     * Get authorities from JWT token.
     * 
     * @param token the JWT token
     * @return the list of granted authorities
     * @throws ParseException if parsing fails
     */
    public List<GrantedAuthority> getAuthoritiesFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        List<String> roles = signedJWT.getJWTClaimsSet().getStringListClaim("roles");
        return roles.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }
}
