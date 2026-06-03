package com.beenotice.demo.application.random;

import com.beenotice.demo.domain.model.SanityCheck;

public record RandomPick(SanityCheck check, int position, int total) {
}
