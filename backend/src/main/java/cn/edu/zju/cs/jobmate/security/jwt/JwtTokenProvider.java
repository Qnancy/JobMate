package cn.edu.zju.cs.jobmate.security.jwt;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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
     * @param userDetails the user details
     * @return the generated JWT token
     * @throws JOSEException if signing fails
     */
    public String generateToken(UserDetails userDetails) throws JOSEException {
        Date now = new Date();
        Date exp = new Date(now.getTime() + properties.getExpiration());

        // Prepare the JWT.
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(userDetails.getUsername())
            .jwtID(UUID.randomUUID().toString())
            .issueTime(now)
            .expirationTime(exp)
            // TODO: .claim()
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
     * Extract username from JWT token.
     * 
     * @param token the JWT token
     * @return the extracted username
     * @throws ParseException if parsing fails
     */
    public String getUsernameFromToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();
    }
}
