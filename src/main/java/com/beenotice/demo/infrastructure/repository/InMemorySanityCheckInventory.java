package com.beenotice.demo.infrastructure.repository;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Component
public class InMemorySanityCheckInventory implements SanityCheckInventory {

    private final List<SanityCheck> sanityChecks;

    public InMemorySanityCheckInventory(@Value("${app.sanity-checks-path}") Resource resource) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        this.sanityChecks = mapper.readValue(resource.getInputStream(), new TypeReference<List<SanityCheck>>() {});
    }

    @Override
    public List<SanityCheck> findAll() {
        return sanityChecks;
    }
}
