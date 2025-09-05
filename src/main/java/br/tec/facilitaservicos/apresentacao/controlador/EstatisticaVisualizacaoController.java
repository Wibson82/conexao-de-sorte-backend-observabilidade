package br.tec.facilitaservicos.observability.apresentacao.controlador;

import br.tec.facilitaservicos.observability.aplicacao.servico.PageViewStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/v1/metricas/visualizacao")
@Tag(name = "Estatística de Visualização", description = "Page views por rota/caminho")
public class EstatisticaVisualizacaoController {

    private final PageViewStatsService statsService;

    public EstatisticaVisualizacaoController(PageViewStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/registrar")
    @Operation(summary = "Registrar visualização de página")
    public Mono<ResponseEntity<Void>> registrar(@RequestParam String path) {
        statsService.record(path);
        return Mono.just(ResponseEntity.ok().build());
    }

    @GetMapping("/snapshot")
    @Operation(summary = "Snapshot de page views por path")
    public Mono<ResponseEntity<Map<String, Long>>> snapshot() {
        return Mono.just(ResponseEntity.ok(statsService.snapshot()));
    }
}

