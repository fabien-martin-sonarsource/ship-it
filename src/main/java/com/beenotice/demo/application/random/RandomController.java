package com.beenotice.demo.application.random;

import com.beenotice.demo.infrastructure.controller.SanityCheckView;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.concurrent.ThreadLocalRandom;

@Controller
public class RandomController {

    private final PickRandomSanityCheck pickRandomSanityCheck;

    public RandomController(PickRandomSanityCheck pickRandomSanityCheck) {
        this.pickRandomSanityCheck = pickRandomSanityCheck;
    }

    @GetMapping("/random")
    public String random(Model model) {
        RandomPick pick = pickRandomSanityCheck.pickRandom(ThreadLocalRandom.current());
        model.addAttribute("sanityCheck", SanityCheckView.from(pick.check()));
        model.addAttribute("bannerText",
                "🎲 Random pick — check %d of %d".formatted(pick.position() + 1, pick.total()));
        return "sanity-check";
    }
}
