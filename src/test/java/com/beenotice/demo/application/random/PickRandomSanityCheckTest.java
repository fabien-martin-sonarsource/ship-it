package com.beenotice.demo.application.random;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PickRandomSanityCheckTest {

    @Mock
    private SanityCheckInventory inventory;

    @InjectMocks
    private PickRandomSanityCheck pickRandomSanityCheck;

    @Test
    void pickRandom_returnsCheckAtRandomPosition() {
        SanityCheck a = check("a");
        SanityCheck b = check("b");
        SanityCheck c = check("c");
        when(inventory.findAll()).thenReturn(List.of(a, b, c));

        RandomGenerator random = mock(RandomGenerator.class);
        when(random.nextInt(3)).thenReturn(1);

        RandomPick pick = pickRandomSanityCheck.pickRandom(random);

        assertThat(pick.check()).isEqualTo(b);
        assertThat(pick.position()).isEqualTo(1);
        assertThat(pick.total()).isEqualTo(3);
    }

    @Test
    void pickRandom_isDeterministicWithSeededRandom() {
        when(inventory.findAll()).thenReturn(List.of(check("a"), check("b"), check("c"), check("d")));

        RandomPick first = pickRandomSanityCheck.pickRandom(new Random(42L));
        RandomPick second = pickRandomSanityCheck.pickRandom(new Random(42L));

        assertThat(first).isEqualTo(second);
    }

    private SanityCheck check(String name) {
        return new SanityCheck(name, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
