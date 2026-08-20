package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.PickSanityCheck;
import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.model.SanityCheckPick;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuiController.class)
class GuiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PickSanityCheck pickSanityCheck;

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
    void root_doesNotShowRandomBanner() throws Exception {
        when(pickSanityCheck.atPosition(0)).thenReturn(anyCheck());

        mockMvc.perform(get("/"))
                .andExpect(model().attributeDoesNotExist("randomPick"))
                .andExpect(content().string(not(containsString("Random pick"))));
    }

    @Test
    void random_returnsSanityCheckViewWithBanner() throws Exception {
        when(pickSanityCheck.random()).thenReturn(new SanityCheckPick(anyCheck(), 2, 5));

        mockMvc.perform(get("/random"))
                .andExpect(status().isOk())
                .andExpect(view().name("sanity-check"))
                .andExpect(model().attributeExists("sanityCheck"))
                .andExpect(model().attribute("randomPick", new RandomPickView(2, 5)))
                .andExpect(content().string(containsString("🎲 Random pick — check 2 of 5")));
    }

    @Test
    void random_doesNotAffectSessionCheckIndex() throws Exception {
        when(pickSanityCheck.random()).thenReturn(new SanityCheckPick(anyCheck(), 1, 3));

        mockMvc.perform(get("/random").sessionAttr("checkIndex", 4))
                .andExpect(model().attribute("checkIndex", 4));
    }

    private SanityCheck anyCheck() {
        return new SanityCheck("context", "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
