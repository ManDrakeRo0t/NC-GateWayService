package ru.bogatov.gatewayservice.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.bogatov.gatewayservice.client.AuthClient;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class AuthenticationFilter extends AbstractNameValueGatewayFilterFactory{

    public static final String AUTH = "Authorization";
    private AuthClient authClient;
    private String secret = "amps";

    AuthenticationFilter (AuthClient authClient){
        this.authClient = authClient;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);

        return response.setComplete();
    }

    private String getTokenFromRequest(ServerHttpRequest request){
        String bearer = request.getHeaders().get(AUTH).get(0);
        if(bearer != null && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }
        return null;
    }

    private boolean isValid(String token){
        return authClient.getIdFormToken(token) != null;
    }

    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if(!request.getHeaders().containsKey(AUTH)){
                return onError(exchange,HttpStatus.UNAUTHORIZED);
            }

            String token = getTokenFromRequest(request);

            if(!isValid(token)){
                return onError(exchange,HttpStatus.UNAUTHORIZED);
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header(AUTH,"Bearer "+editToken(token))
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        });
    }

    private String editToken(String oldToken){
        Claims oldClaims = Jwts.parser().setSigningKey(secret).parseClaimsJws(oldToken).getBody();
        String email = oldClaims.get("email").toString();
        Date expiration = oldClaims.getExpiration();
        Map<String,Object> claims = new HashMap<>();
        claims.put("email",email);
        claims.put("roles",oldClaims.get("roles"));
        claims.put("gateway","valid");
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512,secret)
                .compact();
    }

}
