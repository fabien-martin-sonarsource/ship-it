package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.ImportScenarios;
import com.beenotice.demo.application.PickSanityCheck;
import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.infrastructure.yaml.ScenarioImportException;
import com.beenotice.demo.infrastructure.yaml.ScenarioYamlParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuiController.class)
class GuiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PickSanityCheck pickSanityCheck;

    @MockitoBean
    private ScenarioYamlParser scenarioYamlParser;

    @MockitoBean
    private ImportScenarios importScenarios;

    @Test
    void root_returnsSanityCheckView() throws Exception {
        when(pickSanityCheck.atPosition(0)).thenReturn(anyCheck());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("sanity-check"))
                .andExpect(model().attributeExists("sanityCheck"));
    }

    @Test
    void root_exposesViewDtoNotDomainModel() throws Exception {
        when(pickSanityCheck.atPosition(0)).thenReturn(anyCheck());

        mockMvc.perform(get("/"))
                .andExpect(model().attribute("sanityCheck", org.hamcrest.Matchers.instanceOf(SanityCheckView.class)));
    }

    @Test
    void root_incrementsCheckIndex() throws Exception {
        when(pickSanityCheck.atPosition(0)).thenReturn(anyCheck());
        when(pickSanityCheck.atPosition(1)).thenReturn(anyCheck());

        mockMvc.perform(get("/"))
                .andExpect(model().attribute("checkIndex", 1));

        mockMvc.perform(get("/").sessionAttr("checkIndex", 1))
                .andExpect(model().attribute("checkIndex", 2));
    }

    @Test
    void importScenarios_success_redirectsWithFlashMessage() throws Exception {
        List<SanityCheck> parsed = List.of(anyCheck());
        when(scenarioYamlParser.parse(any())).thenReturn(parsed);
        when(importScenarios.fromScenarios(parsed)).thenReturn(1);
        MockMultipartFile file = new MockMultipartFile("file", "batch.yaml", "text/yaml", "scenarios: []".getBytes());

        mockMvc.perform(multipart("/import").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attribute("importMessage", "Imported 1 scenario from batch.yaml"));
    }

    @Test
    void importScenarios_parseFailure_rendersPageWithInlineError() throws Exception {
        when(pickSanityCheck.atPosition(0)).thenReturn(anyCheck());
        when(scenarioYamlParser.parse(any())).thenThrow(new ScenarioImportException("bad yaml"));
        MockMultipartFile file = new MockMultipartFile("file", "broken.yaml", "text/yaml", "not yaml".getBytes());

        mockMvc.perform(multipart("/import").file(file))
                .andExpect(status().isOk())
                .andExpect(view().name("sanity-check"))
                .andExpect(model().attributeExists("importError"));
    }

    private SanityCheck anyCheck() {
        return new SanityCheck("sc-1", "context", "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
