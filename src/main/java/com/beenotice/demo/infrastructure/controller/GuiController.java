package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.PickRandomSanityCheck;
import com.beenotice.demo.application.PickSanityCheck;
import com.beenotice.demo.domain.model.RandomSanityCheckPick;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

// The cursor through the deck is an HTTP-session concern, not a domain one:
// the use case is stateless and only knows how to pick a check at a given position.
@Controller
@SessionAttributes("checkIndex")
@SuppressWarnings("java:S3753") // The cursor is meant to live for the whole session; there is no point at which it should be cleared.
public class GuiController {

    private final PickSanityCheck pickSanityCheck;
    private final PickRandomSanityCheck pickRandomSanityCheck;

    public GuiController(PickSanityCheck pickSanityCheck, PickRandomSanityCheck pickRandomSanityCheck) {
        this.pickSanityCheck = pickSanityCheck;
        this.pickRandomSanityCheck = pickRandomSanityCheck;
    }

    @ModelAttribute("checkIndex")
    public Integer initCheckIndex() {
        return 0;
    }

    @GetMapping("/")
    public String sanityCheck(@ModelAttribute("checkIndex") Integer checkIndex, Model model) {
        model.addAttribute("sanityCheck", SanityCheckView.from(pickSanityCheck.atPosition(checkIndex)));
        model.addAttribute("checkIndex", checkIndex + 1);
        return "sanity-check";
    }

    @GetMapping("/random")
    public String randomSanityCheck(Model model) {
        RandomSanityCheckPick pick = pickRandomSanityCheck.pick();
        model.addAttribute("sanityCheck", SanityCheckView.from(pick.check()));
        model.addAttribute("randomPick", new RandomPickBanner(pick.position(), pick.total()));
        return "sanity-check";
    }

    public record RandomPickBanner(int position, int total) {}
}
