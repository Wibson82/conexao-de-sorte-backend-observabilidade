package br.tec.facilitaservicos.observability.apresentacao.controlador;

import br.tec.facilitaservicos.observability.aplicacao.servico.ExternalHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/v1/diagnostico")
@Tag(name = "Diagnóstico", description = "Diagnósticos de produção/usuário/cache")
public class DiagnosticoController {

    private final ExternalHealthService externalHealthService;

    public DiagnosticoController(ExternalHealthService externalHealthService) {
        this.externalHealthService = externalHealthService;
    }

    @GetMapping("/producao/info-sistema")
    @Operation(summary = "Info do sistema")
    public Mono<ResponseEntity<Map<String,Object>>> infoSistema() {
        try {
            String host = InetAddress.getLocalHost().getHostName();
            return Mono.just(ResponseEntity.ok(Map.of(
                    "host", host,
                    "timestamp", LocalDateTime.now().toString()
            )));
        } catch (Exception e) {
            return Mono.just(ResponseEntity.ok(Map.of(
                    "host", "unknown",
                    "timestamp", LocalDateTime.now().toString()
            )));
        }
    }

    @GetMapping("/producao/conectividade")
    @Operation(summary = "Conectividade básica (redis/auth)")
    public Mono<ResponseEntity<Map<String,Object>>> conectividade() {
        return Mono.zip(
                externalHealthService.redisPing(),
                externalHealthService.authJwksHealth()
        ).map(tuple -> ResponseEntity.ok(Map.of(
                "redis", tuple.getT1().get("redis"),
                "authJwks", tuple.getT2().get("authJwks")
        )));
    }

    @GetMapping("/producao/status-autenticacao")
    @Operation(summary = "Status da autenticação/JWKS")
    public Mono<ResponseEntity<Map<String,Object>>> statusAutenticacao() {
        return externalHealthService.authJwksHealth().map(ResponseEntity::ok);
    }

    @GetMapping("/cache/health")
    @Operation(summary = "Ping do Redis")
    public Mono<ResponseEntity<Map<String,Object>>> cacheHealth() {
        return externalHealthService.redisPing().map(ResponseEntity::ok);
    }
}
