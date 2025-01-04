package com.microservices.instagrambackend.config;

import com.microservices.instagrambackend.repository.UserRepository;
import com.microservices.instagrambackend.service.JWTService;
import com.microservices.instagrambackend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@RequiredArgsConstructor
public class AuthenticatedHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthenticatedHandshakeInterceptor.class);
    private final UserRepository userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        try {
            if (request instanceof ServletServerHttpRequest servletRequest) {
                // Lấy token từ request
                String token = extractToken(servletRequest);
                if (token == null) return false;

                // Parse JWT và validate
                Claims claims = parseToken(token);
                if (claims == null) return false;

                // Kiểm tra token hết hạn
                if (claims.getExpiration().before(new Date())) {
                    return false;
                }

                // Lấy thông tin từ claims
                String email = claims.getSubject();
                String role = claims.get("scope", String.class);

                // Kiểm tra user trong database
                var userDetails = userService.findByEmail(email).orElse(null);
                if (userDetails == null) {
                    return false;
                }

                // Lưu thông tin vào session attributes
                attributes.put("email", email);
                attributes.put("role", role);
                attributes.put("userDetails", userDetails);

                log.info(attributes.toString());

                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    private String extractToken(ServletServerHttpRequest request) {
        String token = request.getServletRequest().getParameter("token");

        if (token == null) {
            String authHeader = request.getServletRequest().getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        return token;
    }

    private Claims parseToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                    Decoders.BASE64.decode(
                            "QzAxNzFGOTdGRjc3QjcwQTE5ODhCNjQ0QzZCMDE3" +
                                    "RDBEMDQ2MThGQkQ4MDgwQjI2QkRDMzUzRTU" +
                                    "3QzgwRTg2Mg=="
                    )
            );

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
