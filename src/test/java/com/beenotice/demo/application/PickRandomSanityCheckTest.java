package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.RandomSanityCheckPick;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.RandomGenerator;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PickRandomSanityCheckTest {

    @Mock
    private SanityCheckInventory inventory;

    @Test
    void pick_returnsCheckAtSeededRandomIndex() {
        List<SanityCheck> deck = List.of(check("first"), check("second"), check("third"));
        when(inventory.findAll()).thenReturn(deck);

        long seed = 42L;
        int expectedIndex = new Random(seed).nextInt(deck.size());
        PickRandomSanityCheck useCase = new PickRandomSanityCheck(inventory, seededWith(seed));

        RandomSanityCheckPick pick = useCase.pick();

        assertThat(pick.check()).isEqualTo(deck.get(expectedIndex));
        assertThat(pick.position()).isEqualTo(expectedIndex + 1);
        assertThat(pick.total()).isEqualTo(deck.size());
    }

    @Test
    void pick_isFullyDeterminedByGenerator() {
        List<SanityCheck> deck = List.of(check("first"), check("second"), check("third"));
        when(inventory.findAll()).thenReturn(deck);
        RandomGenerator alwaysOne = bound -> 1;

        PickRandomSanityCheck useCase = new PickRandomSanityCheck(inventory, alwaysOne);

        RandomSanityCheckPick pick = useCase.pick();

        assertThat(pick.check()).isEqualTo(deck.get(1));
        assertThat(pick.position()).isEqualTo(2);
        assertThat(pick.total()).isEqualTo(3);
    }

    private RandomGenerator seededWith(long seed) {
        Random random = new Random(seed);
        return random::nextInt;
    }

    private SanityCheck check(String name) {
        return new SanityCheck(name, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
