package com.beenotice.demo.domain.service;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.infrastructure.repository.InMemorySanityCheckInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SanityCheckRunnerImplTest {

    @Mock
    private InMemorySanityCheckInventory sanityCheckInventory;

    @Mock
    private CsvSanityCheckExporter csvExporter;

    @InjectMocks
    private SanityCheckRunnerImpl runner;

    @Test
    void getCheckAt_returnsCheckAtIndex() {
        SanityCheck first = check("first");
        SanityCheck second = check("second");
        when(sanityCheckInventory.findAll()).thenReturn(List.of(first, second));

        assertThat(runner.getCheckAt(0)).isEqualTo(first);
        assertThat(runner.getCheckAt(1)).isEqualTo(second);
    }

    @Test
    void getCheckAt_wrapsAroundWithModulo() {
        SanityCheck only = check("only");
        when(sanityCheckInventory.findAll()).thenReturn(List.of(only));

        assertThat(runner.getCheckAt(0)).isEqualTo(only);
        assertThat(runner.getCheckAt(1)).isEqualTo(only);
        assertThat(runner.getCheckAt(42)).isEqualTo(only);
    }

    private SanityCheck check(String name) {
        return new SanityCheck(name, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
