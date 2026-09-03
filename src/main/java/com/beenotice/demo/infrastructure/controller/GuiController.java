package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.ImportScenarios;
import com.beenotice.demo.application.PickSanityCheck;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.infrastructure.yaml.ScenarioImportException;
import com.beenotice.demo.infrastructure.yaml.ScenarioYamlParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

// The cursor through the deck is an HTTP-session concern, not a domain one:
// the use case is stateless and only knows how to pick a check at a given position.
@Controller
@SessionAttributes("checkIndex")
public class GuiController {

    private final PickSanityCheck pickSanityCheck;
    private final ScenarioYamlParser scenarioYamlParser;
    private final ImportScenarios importScenarios;

    public GuiController(PickSanityCheck pickSanityCheck, ScenarioYamlParser scenarioYamlParser, ImportScenarios importScenarios) {
        this.pickSanityCheck = pickSanityCheck;
        this.scenarioYamlParser = scenarioYamlParser;
        this.importScenarios = importScenarios;
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

    @PostMapping("/import")
    public String handleImport(@RequestParam("file") MultipartFile file,
                                @ModelAttribute("checkIndex") Integer checkIndex,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            List<SanityCheck> parsed = scenarioYamlParser.parse(file.getInputStream());
            int count = importScenarios.fromScenarios(parsed);
            redirectAttributes.addFlashAttribute("importMessage",
                    "Imported %d scenario%s from %s".formatted(count, count == 1 ? "" : "s", file.getOriginalFilename()));
            return "redirect:/";
        } catch (ScenarioImportException | IOException e) {
            model.addAttribute("sanityCheck", SanityCheckView.from(pickSanityCheck.atPosition(checkIndex)));
            model.addAttribute("checkIndex", checkIndex);
            model.addAttribute("importError", "Could not import " + file.getOriginalFilename() + ": " + e.getMessage());
            return "sanity-check";
        }
    }
}
