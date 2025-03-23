package com.demanxier.cards.exception;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")// Aplica a todos os endpoints
                .allowedOrigins("*") // Permite todas as origens
                .allowedMethods("*") // Permite todos os métodos (GET, POST, PUT, etc.)
                .allowedHeaders("*") // Permite todos os cabeçalhos
                .allowCredentials(false) // Não permite credenciais (cookies, autenticação)
                .maxAge(3600); // Cache de opções CORS por 1 hora
    }
}
