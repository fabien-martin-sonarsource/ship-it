package com.beenotice.demo.application.random;

import com.beenotice.demo.domain.model.Decision;
import com.beenotice.demo.domain.model.SanityCheck;
import com.beenotice.demo.infrastructure.controller.SanityCheckView;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.random.RandomGenerator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RandomController.class)
class RandomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PickRandomSanityCheck pickRandomSanityCheck;

    @Test
    void random_returnsSanityCheckViewWithBanner() throws Exception {
        when(pickRandomSanityCheck.pickRandom(any(RandomGenerator.class)))
                .thenReturn(new RandomPick(anyCheck(), 3, 8));

        mockMvc.perform(get("/random"))
                .andExpect(status().isOk())
                .andExpect(view().name("sanity-check"))
                .andExpect(model().attribute("bannerText", "🎲 Random pick — check 4 of 8"))
                .andExpect(model().attribute("sanityCheck", Matchers.instanceOf(SanityCheckView.class)));
    }

    private SanityCheck anyCheck() {
        return new SanityCheck("context", "question?", new Decision("A", "consequence A"), new Decision("B", "consequence B"));
    }
}
