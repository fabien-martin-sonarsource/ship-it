package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.domain.model.SanityCheckPick;

public record RandomPickView(int position, int total) {

    public static RandomPickView from(SanityCheckPick pick) {
        return new RandomPickView(pick.position(), pick.total());
    }
}
