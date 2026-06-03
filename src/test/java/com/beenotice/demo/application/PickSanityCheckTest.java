package com.beenotice.demo.application;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    private SanityCheck check(String name) {
        return new SanityCheck(name, "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
