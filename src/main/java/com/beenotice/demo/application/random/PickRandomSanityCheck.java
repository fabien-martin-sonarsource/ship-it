package com.beenotice.demo.application.random;

import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.random.RandomGenerator;

@Service
public final class PickRandomSanityCheck {

    private final SanityCheckInventory inventory;

    public PickRandomSanityCheck(SanityCheckInventory inventory) {
        this.inventory = inventory;
    }

    public RandomPick pickRandom(RandomGenerator random) {
        List<SanityCheck> all = inventory.findAll();
        int position = random.nextInt(all.size());
        return new RandomPick(all.get(position), position, all.size());
    }
}
