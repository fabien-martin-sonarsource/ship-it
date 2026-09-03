package com.beenotice.demo.infrastructure.repository;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemorySanityCheckInventoryTest {

    private static final String SEED_JSON = """
            [{"id":"seed","context":"seed context","question":"seed question?",
              "optionA":{"label":"A","consequence":"consequence A"},
              "optionB":{"label":"B","consequence":"consequence B"}}]
            """;

    @Test
    void merge_appendsNewScenario() throws Exception {
        InMemorySanityCheckInventory inventory = newInventory();

        inventory.merge(List.of(check("new-1", "new context")));

        assertThat(inventory.findAll()).extracting(SanityCheck::id).containsExactly("seed", "new-1");
    }

    @Test
    void merge_collidingId_incomingScenarioWins() throws Exception {
        InMemorySanityCheckInventory inventory = newInventory();

        inventory.merge(List.of(check("seed", "replaced context")));

        assertThat(inventory.findAll()).hasSize(1);
        assertThat(inventory.findAll().get(0).context()).isEqualTo("replaced context");
    }

    private InMemorySanityCheckInventory newInventory() throws Exception {
        return new InMemorySanityCheckInventory(new ByteArrayResource(SEED_JSON.getBytes(StandardCharsets.UTF_8)));
    }

    private SanityCheck check(String id, String context) {
        return new SanityCheck(id, context, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
