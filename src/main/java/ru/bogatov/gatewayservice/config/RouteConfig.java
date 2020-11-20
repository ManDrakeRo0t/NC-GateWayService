package ru.bogatov.gatewayservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.bogatov.gatewayservice.filter.AuthenticationFilter;

@Configuration
public class RouteConfig {

    private AuthenticationFilter authenticationFilter;

    public RouteConfig(@Autowired AuthenticationFilter authenticationFilter){
        this.authenticationFilter = authenticationFilter;
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("customerModule",p -> p.path("/customers/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AbstractNameValueGatewayFilterFactory.NameValueConfig())))
                        .uri("http://localhost:8091/customers"))
                .route("orderModule",p -> p.path("/orders/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AbstractNameValueGatewayFilterFactory.NameValueConfig())))
                        .uri("http://localhost:8092/orders"))
                .route("offerModule",p -> p.path("/offers/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AbstractNameValueGatewayFilterFactory.NameValueConfig())))
                        .uri("http://localhost:8093/offers"))
                .route("authModule",p -> p.path("/auth/**")
                        .uri("http://localhost:8091/auth"))
                .build();
    }
}
