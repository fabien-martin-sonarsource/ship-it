package com.beenotice.demo.domain.model;

public record SanityCheck(String id, String context, String question, Decision optionA, Decision optionB) {
}
