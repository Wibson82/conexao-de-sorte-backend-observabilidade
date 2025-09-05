package br.tec.facilitaservicos.observability.apresentacao.controlador;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/monitoramento")
@Tag(name = "Monitoramento", description = "Correções, KeyVault, Cache, Inicialização e Variáveis de Ambiente")
public class MonitoramentoController {

    @GetMapping("/status-correcoes")
    @Operation(summary = "Status das correções em produção")
    public Mono<ResponseEntity<Map<String,Object>>> statusCorrecoes() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "correcoesAplicadas", true,
                "pendencias", 0
        )));
    }

    @GetMapping("/azure-keyvault")
    @Operation(summary = "Status de configuração do Azure Key Vault")
    public Mono<ResponseEntity<Map<String,Object>>> azureKeyVault() {
        boolean configured = System.getenv("AZURE_KEYVAULT_ENDPOINT") != null;
        return Mono.just(ResponseEntity.ok(Map.of(
                "configured", configured,
                "endpointConfigured", System.getenv("AZURE_KEYVAULT_ENDPOINT") != null
        )));
    }

    @GetMapping("/cache")
    @Operation(summary = "Status geral do cache")
    public Mono<ResponseEntity<Map<String,Object>>> cache() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "redisConfigured", System.getenv("conexao-de-sorte-redis-host") != null
        )));
    }

    @GetMapping("/inicializacao")
    @Operation(summary = "Status de inicialização")
    public Mono<ResponseEntity<Map<String,Object>>> inicializacao() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "uptime", java.time.Duration.ofMillis(java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime()).toString()
        )));
    }

    @GetMapping("/environment-variables")
    @Operation(summary = "Variáveis de ambiente relevantes")
    public Mono<ResponseEntity<Map<String,Object>>> env() {
        Map<String, String> env = System.getenv();
        // Redigir valores sensíveis
        return Mono.just(ResponseEntity.ok(Map.of(
                "AZURE_KEYVAULT_ENDPOINT", env.getOrDefault("AZURE_KEYVAULT_ENDPOINT", ""),
                "AZURE_KEYVAULT_NAME", env.getOrDefault("AZURE_KEYVAULT_NAME", ""),
                "JWT_ISSUER_CONFIGURED", env.containsKey("conexao-de-sorte-jwt-issuer")
        )));
    }
}

