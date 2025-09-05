package br.tec.facilitaservicos.observability.apresentacao.controlador;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/metricas")
@Tag(name = "Métricas", description = "Exposição de métricas da aplicação")
public class MetricasController {

    private final MeterRegistry registry;

    public MetricasController(MeterRegistry registry) {
        this.registry = registry;
    }

    @GetMapping
    @Operation(summary = "Snapshot de métricas")
    public Mono<ResponseEntity<Map<String,Object>>> snapshot() {
        var meters = registry.getMeters().stream().map(Meter::getId).collect(Collectors.toList());
        var names = meters.stream().map(Meter.Id::getName).distinct().sorted().toList();
        return Mono.just(ResponseEntity.ok(Map.of(
                "totalMeters", meters.size(),
                "names", names
        )));
    }

    @GetMapping("/contadores")
    @Operation(summary = "Lista de contadores")
    public Mono<ResponseEntity<List<String>>> contadores() {
        var counters = registry.getMeters().stream()
                .filter(m -> m.getId().getType() == Meter.Type.COUNTER)
                .map(m -> m.getId().getName())
                .distinct().sorted().toList();
        return Mono.just(ResponseEntity.ok(counters));
    }

    @GetMapping("/timers")
    @Operation(summary = "Lista de timers")
    public Mono<ResponseEntity<List<String>>> timers() {
        var timers = registry.getMeters().stream()
                .filter(m -> m.getId().getType() == Meter.Type.TIMER)
                .map(m -> m.getId().getName())
                .distinct().sorted().toList();
        return Mono.just(ResponseEntity.ok(timers));
    }
}

