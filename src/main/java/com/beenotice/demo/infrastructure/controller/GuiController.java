package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.PickSanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

// The cursor through the deck is an HTTP-session concern, not a domain one:
// the use case is stateless and only knows how to pick a check at a given position.
@Controller
@SessionAttributes("checkIndex")
public class GuiController {

    private final PickSanityCheck pickSanityCheck;
    private final SanityCheckInventory inventory;

    public GuiController(PickSanityCheck pickSanityCheck, SanityCheckInventory inventory) {
        this.pickSanityCheck = pickSanityCheck;
        this.inventory = inventory;
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

    @GetMapping("/count")
    @ResponseBody
    public int count() {
        return inventory.findAll().size();
    }
}
