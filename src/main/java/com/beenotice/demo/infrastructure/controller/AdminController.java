package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final SanityCheckInventory sanityCheckInventory;

    public AdminController(SanityCheckInventory sanityCheckInventory) {
        this.sanityCheckInventory = sanityCheckInventory;
    }

    @GetMapping("/scenarios")
    public String listScenarios(Model model) {
        List<SanityCheckView> scenarios = sanityCheckInventory.findAll().stream()
                .map(SanityCheckView::from)
                .toList();
        model.addAttribute("scenarios", scenarios);
        return "admin/scenarios";
    }
}
