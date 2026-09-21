package com.beenotice.demo.infrastructure.repository;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemorySanityCheckInventory implements SanityCheckInventory {

    private final Map<String, SanityCheck> sanityChecks = new LinkedHashMap<>();

    public InMemorySanityCheckInventory(@Value("${app.sanity-checks-path}") Resource resource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<SanityCheck> initial = mapper.readValue(resource.getInputStream(), new TypeReference<List<SanityCheck>>() {});
        merge(initial);
    }

    @Override
    public synchronized List<SanityCheck> findAll() {
        return List.copyOf(sanityChecks.values());
    }

    @Override
    public synchronized void merge(List<SanityCheck> scenarios) {
        for (SanityCheck scenario : scenarios) {
            sanityChecks.put(scenario.id(), scenario);
        }
    }
}
