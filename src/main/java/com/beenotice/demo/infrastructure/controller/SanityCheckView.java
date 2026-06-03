package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.domain.model.SanityCheck;

public record SanityCheckView(
        String context,
        String question,
        DecisionView optionA,
        DecisionView optionB) {

    public record DecisionView(String label, String consequence) {}

    public static SanityCheckView from(SanityCheck check) {
        return new SanityCheckView(
                check.context(),
                check.question(),
                new DecisionView(check.optionA().label(), check.optionA().consequence()),
                new DecisionView(check.optionB().label(), check.optionB().consequence()));
    }
}
