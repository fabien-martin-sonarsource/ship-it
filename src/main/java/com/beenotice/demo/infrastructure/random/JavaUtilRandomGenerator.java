package com.beenotice.demo.infrastructure.random;

import com.beenotice.demo.domain.spi.RandomGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class JavaUtilRandomGenerator implements RandomGenerator {

    @Override
    @SuppressWarnings("java:S2245") // Non-security use: shuffling a UI deck of sanity checks.
    public int nextIntBelow(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }
}
