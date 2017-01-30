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
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationToken implements Authentication {
    private static final long serialVersionUID = 1L;
    private JWT token;
    private boolean authenticated;
    private JWTClaimsSet claims;
    private Collection<? extends GrantedAuthority> authorities = Collections.emptyList();
    private User details;
    private String uuid;

    public JwtAuthenticationToken(JWT token) {
        this.token = token;
        try {
            this.claims = token.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }
    
    public JWT getToken() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getName() {
        return uuid;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities  = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    public void setDetails(User details) {
        this.details = details;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;
    }

    public JWTClaimsSet getClaims() {
        return claims;
    }
}
