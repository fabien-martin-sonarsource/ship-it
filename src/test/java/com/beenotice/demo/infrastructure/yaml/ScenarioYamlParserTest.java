package com.beenotice.demo.infrastructure.yaml;

import com.beenotice.demo.domain.model.SanityCheck;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScenarioYamlParserTest {

    private final ScenarioYamlParser parser = new ScenarioYamlParser();

    @Test
    void parse_returnsScenariosFromWellFormedYaml() {
        String yaml = """
                scenarios:
                  - context: "A contributor uploads a batch of scenarios."
                    question: "Do you trust the file?"
                    optionA:
                      label: "Trust it blindly"
                      consequence: "Bad things happen"
                    optionB:
                      label: "Validate first"
                      consequence: "All good"
                """;

        List<SanityCheck> scenarios = parser.parse(inputStream(yaml));

        assertThat(scenarios).hasSize(1);
        SanityCheck scenario = scenarios.get(0);
        assertThat(scenario.id()).isNotBlank();
        assertThat(scenario.context()).isEqualTo("A contributor uploads a batch of scenarios.");
        assertThat(scenario.question()).isEqualTo("Do you trust the file?");
        assertThat(scenario.optionA().label()).isEqualTo("Trust it blindly");
        assertThat(scenario.optionB().consequence()).isEqualTo("All good");
    }

    @Test
    void parse_rejectsMissingScenariosList() {
        assertThatThrownBy(() -> parser.parse(inputStream("not-scenarios: []")))
                .isInstanceOf(ScenarioImportException.class);
    }

    @Test
    void parse_rejectsEntryMissingRequiredField() {
        String yaml = """
                scenarios:
                  - context: "context only"
                """;

        assertThatThrownBy(() -> parser.parse(inputStream(yaml)))
                .isInstanceOf(ScenarioImportException.class);
    }

    @Test
    void parse_rejectsMalformedYaml() {
        assertThatThrownBy(() -> parser.parse(inputStream("scenarios: [")))
                .isInstanceOf(ScenarioImportException.class);
    }

    private InputStream inputStream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
