package com.beenotice.demo.infrastructure.random;

import com.beenotice.demo.domain.spi.RandomGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

@Component
public class SecureRandomGenerator implements RandomGenerator {

    private final SecureRandom secureRandom;

    public SecureRandomGenerator() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        try {
            this.secureRandom = SecureRandom.getInstance("DEFAULT", BouncyCastleProvider.PROVIDER_NAME);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new IllegalStateException("Unable to initialize the Bouncy Castle secure random generator", e);
        }
    }

    @Override
    public int nextInt(int bound) {
        return secureRandom.nextInt(bound);
    }
}
