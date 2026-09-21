package com.beenotice.demo.infrastructure.yaml;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Reads an uploaded scenario file straight from YAML into domain objects.
 * Uses {@link SafeConstructor} rather than the default constructor so an
 * uploaded file (unauthenticated, untrusted input) cannot instantiate
 * arbitrary Java types.
 */
@Component
public class ScenarioYamlParser {

    private static final String SCENARIOS_FIELD = "scenarios";
    private static final String OPTION_A_FIELD = "optionA";
    private static final String OPTION_B_FIELD = "optionB";

    public List<SanityCheck> parse(InputStream yamlContent) {
        Object root = load(yamlContent);
        if (!(root instanceof Map<?, ?> rootMap) || !(rootMap.get(SCENARIOS_FIELD) instanceof List<?> entries)) {
            throw new ScenarioImportException("expected a top-level 'scenarios' list");
        }

        List<SanityCheck> scenarios = new ArrayList<>();
        for (Object entry : entries) {
            scenarios.add(toSanityCheck(entry));
        }
        return scenarios;
    }

    private Object load(InputStream yamlContent) {
        try {
            return new Yaml(new SafeConstructor(new LoaderOptions())).load(yamlContent);
        } catch (YAMLException e) {
            throw new ScenarioImportException("could not parse YAML: " + e.getMessage(), e);
        }
    }

    private SanityCheck toSanityCheck(Object entry) {
        if (!(entry instanceof Map<?, ?> fields)) {
            throw new ScenarioImportException("each scenario entry must be a mapping");
        }
        return new SanityCheck(
                UUID.randomUUID().toString(),
                requireText(fields, "context"),
                requireText(fields, "question"),
                toDecision(fields, OPTION_A_FIELD),
                toDecision(fields, OPTION_B_FIELD));
    }

    private Decision toDecision(Map<?, ?> fields, String field) {
        if (!(fields.get(field) instanceof Map<?, ?> decisionFields)) {
            throw new ScenarioImportException("scenario is missing the '" + field + "' mapping");
        }
        return new Decision(requireText(decisionFields, "label"), requireText(decisionFields, "consequence"));
    }

    private String requireText(Map<?, ?> fields, String field) {
        if (!(fields.get(field) instanceof String text) || text.isBlank()) {
            throw new ScenarioImportException("scenario is missing a non-blank '" + field + "' field");
        }
        return text;
    }
}
