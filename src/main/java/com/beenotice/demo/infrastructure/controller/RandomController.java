package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.random.PickRandomSanityCheck;
import com.beenotice.demo.application.random.RandomPickResult;
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
        RandomPickResult result = pickRandomSanityCheck.pickRandom(ThreadLocalRandom.current());
        model.addAttribute("sanityCheck", result.view());
        model.addAttribute("bannerText", result.bannerText());
        return "sanity-check";
    }
}
