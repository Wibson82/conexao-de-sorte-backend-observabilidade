package br.tec.facilitaservicos.observability.aplicacao.servico;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BusinessMetricsService {
    private final MeterRegistry registry;
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    public BusinessMetricsService(MeterRegistry registry) {
        this.registry = registry;
    }

    public void increment(String name) {
        counters.computeIfAbsent(name, n -> Counter.builder("business." + n)
                .description("Business counter: " + n)
                .register(registry))
                .increment();
    }

    public Map<String, Double> snapshot() {
        return counters.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().count()));
    }
}

