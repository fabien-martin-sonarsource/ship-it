package com.beenotice.demo.application.random;

import com.beenotice.demo.infrastructure.controller.SanityCheckView;

public record RandomPickResult(SanityCheckView view, String bannerText) {
}
