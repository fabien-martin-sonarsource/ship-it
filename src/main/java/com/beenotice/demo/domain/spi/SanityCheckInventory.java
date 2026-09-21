package com.beenotice.demo.domain.spi;

import com.beenotice.demo.domain.model.SanityCheck;

import java.util.List;

public interface SanityCheckInventory {
    List<SanityCheck> findAll();

    /**
     * Appends the given scenarios to the deck. When a scenario reuses the id
     * of an existing one, the incoming scenario replaces it in place.
     */
    void merge(List<SanityCheck> scenarios);
}
