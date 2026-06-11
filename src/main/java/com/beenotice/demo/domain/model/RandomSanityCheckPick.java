package com.beenotice.demo.domain.model;

// `position` is 1-based: it is the human-readable rank of the picked check in the deck.
public record RandomSanityCheckPick(SanityCheck check, int position, int total) {
}
