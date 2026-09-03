package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class ImportScenarios {

    private final SanityCheckInventory inventory;

    public ImportScenarios(SanityCheckInventory inventory) {
        this.inventory = inventory;
    }

    public int fromScenarios(List<SanityCheck> scenarios) {
        inventory.merge(scenarios);
        return scenarios.size();
    }
}
