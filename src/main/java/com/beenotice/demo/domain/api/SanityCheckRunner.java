package com.beenotice.demo.domain.api;

import com.beenotice.demo.domain.model.SanityCheck;

public interface SanityCheckRunner {
    SanityCheck getCheckAt(int index);

    String exportAllAsCsv();
}
