package com.beenotice.demo.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SanityCheckDeckTest {

    @Test
    void pickAt_returnsCheckAtPosition() {
        SanityCheck first = check("first");
        SanityCheck second = check("second");
        SanityCheckDeck deck = new SanityCheckDeck(List.of(first, second));

        assertThat(deck.pickAt(0)).isEqualTo(first);
        assertThat(deck.pickAt(1)).isEqualTo(second);
    }

    @Test
    void pickAt_wrapsAroundWithModulo() {
        SanityCheck only = check("only");
        SanityCheckDeck deck = new SanityCheckDeck(List.of(only));

        assertThat(deck.pickAt(0)).isEqualTo(only);
        assertThat(deck.pickAt(1)).isEqualTo(only);
        assertThat(deck.pickAt(42)).isEqualTo(only);
    }

    @Test
    void pickAt_handlesNegativePositions() {
        SanityCheck first = check("first");
        SanityCheck second = check("second");
        SanityCheckDeck deck = new SanityCheckDeck(List.of(first, second));

        assertThat(deck.pickAt(-1)).isEqualTo(second);
        assertThat(deck.pickAt(-2)).isEqualTo(first);
    }

    @Test
    void constructor_rejectsEmptyDeck() {
        List<SanityCheck> emptyList = List.of();
        assertThatThrownBy(() -> new SanityCheckDeck(emptyList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void pickRandom_drawsIndexFromGeneratorAndExposesOneBasedPosition() {
        SanityCheck first = check("first");
        SanityCheck second = check("second");
        SanityCheck third = check("third");
        SanityCheckDeck deck = new SanityCheckDeck(List.of(first, second, third));

        RandomSanityCheckPick pick = deck.pickRandom(bound -> {
            assertThat(bound).isEqualTo(3);
            return 2;
        });

        assertThat(pick.check()).isEqualTo(third);
        assertThat(pick.position()).isEqualTo(3);
        assertThat(pick.total()).isEqualTo(3);
    }

    private SanityCheck check(String name) {
        return new SanityCheck(name, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
