package com.beenotice.demo.domain.service;

import com.beenotice.demo.domain.model.SanityCheck;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CsvSanityCheckExporter {

    private static final String HEADER = "context,question,optionA,optionB";

    public String toCsv(List<SanityCheck> sanityChecks) {
        String rows = sanityChecks.stream()
                .map(this::toCsvRow)
                .collect(Collectors.joining("\n"));
        return HEADER + "\n" + rows;
    }

    private String toCsvRow(SanityCheck check) {
        return String.join(",",
                escape(check.context()),
                escape(check.question()),
                escape(check.optionA().label()),
                escape(check.optionB().label()));
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ").replace("\n", " ");
    }
}
