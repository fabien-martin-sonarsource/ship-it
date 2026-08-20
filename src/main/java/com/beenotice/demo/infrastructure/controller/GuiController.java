package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.PickSanityCheck;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

// The cursor through the deck is an HTTP-session concern, not a domain one:
// the use case is stateless and only knows how to pick a check at a given position.
@Controller
@SessionAttributes("checkIndex")
public class GuiController {

    private final PickSanityCheck pickSanityCheck;

    public GuiController(PickSanityCheck pickSanityCheck) {
        this.pickSanityCheck = pickSanityCheck;
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
        var pick = pickSanityCheck.random();
        model.addAttribute("sanityCheck", SanityCheckView.from(pick.check()));
        model.addAttribute("randomPick", RandomPickView.from(pick));
        return "sanity-check";
    }
}
