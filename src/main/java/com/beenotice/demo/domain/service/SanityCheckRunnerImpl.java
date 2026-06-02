package com.beenotice.demo.domain.service;

import com.beenotice.demo.domain.api.SanityCheckRunner;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.infrastructure.repository.InMemorySanityCheckInventory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SanityCheckRunnerImpl implements SanityCheckRunner {

    private final InMemorySanityCheckInventory sanityCheckInventory;
    private final CsvSanityCheckExporter csvExporter;

    public SanityCheckRunnerImpl(InMemorySanityCheckInventory sanityCheckInventory,
                                 CsvSanityCheckExporter csvExporter) {
        this.sanityCheckInventory = sanityCheckInventory;
        this.csvExporter = csvExporter;
    }

    @Override
    public SanityCheck getCheckAt(int index) {
        List<SanityCheck> sanityChecks = sanityCheckInventory.findAll();
        return sanityChecks.get(index % sanityChecks.size());
    }

    @Override
    public String exportAllAsCsv() {
        return csvExporter.toCsv(sanityCheckInventory.findAll());
    }
}
