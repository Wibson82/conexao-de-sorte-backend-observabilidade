package br.tec.facilitaservicos.observability.aplicacao.servico;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class ExternalHealthService {

    private final WebClient authWebClient;
    private final ReactiveStringRedisTemplate redisTemplate;

    public ExternalHealthService(WebClient authWebClient,
                                 ReactiveRedisConnectionFactory connectionFactory) {
        this.authWebClient = authWebClient;
        this.redisTemplate = new ReactiveStringRedisTemplate(connectionFactory);
    }

    public Mono<Map<String, Object>> authJwksHealth() {
        return authWebClient.get()
                .uri("/oauth2/jwks/health")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Map.class)
                .map(body -> Map.of("authJwks", "UP", "details", body))
                .onErrorResume(ex -> Mono.just(Map.of("authJwks", "DOWN", "error", ex.getMessage())));
    }

    public Mono<Map<String, Object>> redisPing() {
        return redisTemplate.getConnectionFactory().getReactiveConnection()
                .serverCommands().ping()
                .map(resp -> Map.of("redis", "UP", "pong", resp))
                .onErrorResume(ex -> Mono.just(Map.of("redis", "DOWN", "error", ex.getMessage())));
    }
}

