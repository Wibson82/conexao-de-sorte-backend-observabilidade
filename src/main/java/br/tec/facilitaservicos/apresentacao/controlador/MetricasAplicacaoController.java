package br.tec.facilitaservicos.observability.apresentacao.controlador;

import br.tec.facilitaservicos.observability.aplicacao.servico.BusinessMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/v1/admin/metricas")
@Tag(name = "Métricas de Aplicação", description = "Métricas de negócio customizadas")
public class MetricasAplicacaoController {

    private final BusinessMetricsService service;

    public MetricasAplicacaoController(BusinessMetricsService service) {
        this.service = service;
    }

    @GetMapping("/aplicacao")
    @Operation(summary = "Snapshot de métricas de negócio")
    public Mono<ResponseEntity<Map<String, Double>>> snapshot() {
        return Mono.just(ResponseEntity.ok(service.snapshot()));
    }

    @GetMapping("/contador/{nome}/incrementar")
    @Operation(summary = "Incrementar um contador de negócio")
    public Mono<ResponseEntity<Void>> incrementar(@PathVariable String nome) {
        service.increment(nome);
        return Mono.just(ResponseEntity.ok().build());
    }

    @PostMapping("/evento")
    @Operation(summary = "Registrar evento de negócio")
    public Mono<ResponseEntity<Void>> evento(@RequestBody Map<String, String> req) {
        String nome = req.getOrDefault("nome", "evento.generico");
        service.increment(nome);
        return Mono.just(ResponseEntity.ok().build());
    }
}

