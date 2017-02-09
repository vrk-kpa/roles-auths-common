/**
 * The MIT License
 * Copyright (c) 2016 Population Register Centre
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fi.vm.kapa.rova.auth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.*;
import fi.vm.kapa.rova.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;

import static fi.vm.kapa.rova.logging.Logger.Field.*;

public abstract class AbstractJwtAuthenticationManager implements AuthenticationManager {

    private static final Logger LOG = Logger.getLogger(AbstractJwtAuthenticationManager.class);

    private JWSVerifier verifier;

    @Value("${jwt_keystore_path:}")
    private String keystoreFile;

    @Value("${jwt_keystore_pass:}")
    private String keystorePassword;

    @Value("${jwt_token_signing_key_alias:}")
    private String keystoreAlias;

    @Value("${jwt_shared_secret:}")
    private String sharedSecret;

    @PostConstruct
    public void afterPropertiesSet() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, JOSEException {

        if (!StringUtils.isEmpty(sharedSecret)) {
            this.verifier = new MACVerifier(sharedSecret);
        } else {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());

            Key key = keystore.getKey(keystoreAlias, keystorePassword.toCharArray());
            if (key instanceof PrivateKey) {
                // Get certificate of public key
                Certificate cert = keystore.getCertificate(keystoreAlias);

                // Get public key
                RSAPublicKey publicKey = (RSAPublicKey) cert.getPublicKey();
                this.verifier = new RSASSAVerifier(publicKey);
            }
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        JWT jwt = authenticationToken.getToken();
        
        if (jwt instanceof PlainJWT) {
            handlePlainToken((PlainJWT) jwt);
        } else if (jwt instanceof SignedJWT) {
            handleSignedToken((SignedJWT) jwt);
        } else if (jwt instanceof EncryptedJWT) {
            handleEncryptedToken((EncryptedJWT) jwt);
        }
        
        Date referenceTime = new Date();
        JWTClaimsSet claims = authenticationToken.getClaims();
        
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || expirationTime.before(referenceTime)) {
            throw new JwtInvalidTokenException("The token is expired");
        }
        
        Date notBeforeTime = claims.getNotBeforeTime();
        if (notBeforeTime != null && notBeforeTime.after(referenceTime)) {
            throw new JwtInvalidTokenException("Not before is after sysdate");
        }

        String ssn = parseSSN(jwt);
        String assertion = parseAssertion(jwt, ssn);
        try {
            Assert.notNull(ssn, "Missing claim 'hetu' from authentication token");
            
            User userDetails = loadUserDetails(jwt.getJWTClaimsSet());
            if (userDetails == null) {
                throw new AuthenticationCredentialsNotFoundException("Cannot create user from JWT claims");
            }

            authenticationToken.setDetails(userDetails);
            authenticationToken.setAuthorities(userDetails.getAuthorities());
            authenticationToken.setUuid(userDetails.getUsername());
            authenticationToken.setAuthenticated(true);

            LOG.infoMap()
                .set(END_USER, userDetails.getUsername())
                .set(ACTION, "login")
                .set(MSG, "success")
                .set(AUTHENTICATION_ASSERTION, assertion)
                .log();
        } catch (ParseException | RuntimeException e) {
            authenticationToken.setAuthenticated(false);
            authenticationToken.setAuthorities(Collections.emptyList());
            LOG.errorMap()
                .set(END_USER, ssn)
                .set(ACTION, "login")
                .set(MSG, "fail")
                .set(ERRORSTR, e.getMessage())
                .set(AUTHENTICATION_ASSERTION, assertion)
                .log();
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        
        return authenticationToken;
    }

    private String parseSSN(JWT jwt) {
        try {
            return jwt.getJWTClaimsSet().getStringClaim("hetu");
        } catch (ParseException e) {
            LOG.errorMap()
                    .set(END_USER, "(unknown)")
                    .set(ACTION, "login")
                    .set(MSG, "fail")
                    .set(ERRORSTR, e.getMessage())
                    .log();
            throw new AuthenticationCredentialsNotFoundException(e.getMessage(), e);
        }
    }

    private String parseAssertion(JWT jwt, String ssn) {
        String assertion;
        try {
            assertion = jwt.getJWTClaimsSet().getStringClaim("samlAssertionID");
        } catch (ParseException e) {
            LOG.errorMap()
                    .set(END_USER, ssn)
                    .set(ACTION, "login")
                    .set(MSG, "fail")
                    .set(ERRORSTR, e.getMessage())
                    .log();
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        if (StringUtils.isEmpty(assertion)) {
            assertion = jwt.getParsedString();
        }
        return assertion;
    }

    protected abstract User loadUserDetails(JWTClaimsSet claims) throws ParseException;
    
    private void handlePlainToken(PlainJWT jwt) {
        throw new JwtInvalidTokenException("Unsecured plain tokens are not supported");
    }
    
    private void handleSignedToken(SignedJWT jwt) {
        try {
            if (!jwt.verify(verifier)) {
                throw new JwtInvalidTokenException("Signature validation failed");
            }
        } catch (JOSEException e) {
            throw new JwtInvalidTokenException("Signature validation failed");
        }
    }
    
    private void handleEncryptedToken(EncryptedJWT jwt) {
        throw new UnsupportedOperationException("Unsupported token type");
    }
}
