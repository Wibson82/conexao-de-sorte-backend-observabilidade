package br.tec.facilitaservicos.observability.aplicacao.servico;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PageViewStatsService {
    private final Map<String, Long> views = new ConcurrentHashMap<>();

    public void record(String path) {
        if (path == null || path.isBlank()) return;
        views.merge(path, 1L, Long::sum);
    }

    public Map<String, Long> snapshot() {
        return java.util.Collections.unmodifiableMap(views);
    }
}

