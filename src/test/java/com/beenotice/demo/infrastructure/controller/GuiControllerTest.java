package com.beenotice.demo.infrastructure.controller;

import com.beenotice.demo.application.PickSanityCheck;
import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.domain.spi.SanityCheckInventory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuiController.class)
class GuiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PickSanityCheck pickSanityCheck;

    @MockitoBean
    private SanityCheckInventory inventory;

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
    void count_returnsInventorySize() throws Exception {
        when(inventory.findAll()).thenReturn(List.of(anyCheck(), anyCheck()));

        mockMvc.perform(get("/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    private SanityCheck anyCheck() {
        return new SanityCheck("context", "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
