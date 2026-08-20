package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.model.SanityCheckPick;
import com.beenotice.demo.domain.spi.RandomGenerator;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PickSanityCheckTest {

    @Mock
    private SanityCheckInventory inventory;

    @InjectMocks
    private PickSanityCheck pickSanityCheck;

    @Test
    void atPosition_returnsCheckAtIndex() {
        SanityCheck first = check("first");
        SanityCheck second = check("second");
        when(inventory.findAll()).thenReturn(List.of(first, second));

        assertThat(pickSanityCheck.atPosition(0)).isEqualTo(first);
        assertThat(pickSanityCheck.atPosition(1)).isEqualTo(second);
    }

    @Test
    void atPosition_wrapsAround() {
        SanityCheck only = check("only");
        when(inventory.findAll()).thenReturn(List.of(only));

        assertThat(pickSanityCheck.atPosition(0)).isEqualTo(only);
        assertThat(pickSanityCheck.atPosition(1)).isEqualTo(only);
        assertThat(pickSanityCheck.atPosition(42)).isEqualTo(only);
    }

    @Test
    void random_returnsDeterministicPickForSeededGenerator() {
        SanityCheck first = check("first");
        SanityCheck second = check("second");
        SanityCheck third = check("third");
        List<SanityCheck> checks = List.of(first, second, third);
        when(inventory.findAll()).thenReturn(checks);

        long seed = 42L;
        RandomGenerator seeded = bound -> new Random(seed).nextInt(bound);
        PickSanityCheck useCase = new PickSanityCheck(inventory, seeded);

        SanityCheckPick pick = useCase.random();

        int expectedIndex = new Random(seed).nextInt(checks.size());
        assertThat(pick.check()).isEqualTo(checks.get(expectedIndex));
        assertThat(pick.position()).isEqualTo(expectedIndex + 1);
        assertThat(pick.total()).isEqualTo(3);
    }

    private SanityCheck check(String name) {
        return new SanityCheck(name, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
