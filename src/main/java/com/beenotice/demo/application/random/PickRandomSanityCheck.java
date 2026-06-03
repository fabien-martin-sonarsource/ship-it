package com.beenotice.demo.application.random;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import com.beenotice.demo.infrastructure.controller.SanityCheckView;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public final class PickRandomSanityCheck {

    private final SanityCheckInventory inventory;

    public PickRandomSanityCheck(SanityCheckInventory inventory) {
        this.inventory = inventory;
    }

    public RandomPickResult pickRandom(RandomGenerator random) {
        List<SanityCheck> all = inventory.findAll();
        int position = random.nextInt(all.size());
        SanityCheck picked = all.get(position);
        String banner = "🎲 Random pick — check %d of %d".formatted(position + 1, all.size());
        return new RandomPickResult(SanityCheckView.from(picked), banner);
    }
}
