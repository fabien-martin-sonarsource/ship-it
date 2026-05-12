package com.beenotice.demo.infrastructure.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.security.user.name=itadmin",
        "spring.security.user.password={noop}itsecret",
        "spring.security.user.roles=ADMIN"
})
class AdminScenariosHttpBasicIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void adminScenarios_withoutCredentials_returns401() throws Exception {
        mockMvc.perform(get("/admin/scenarios"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminScenarios_withValidBasicAuth_returns200AndScenarioContent() throws Exception {
        mockMvc.perform(get("/admin/scenarios").with(httpBasic("itadmin", "itsecret")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sanity check scenarios")))
                .andExpect(content().string(containsString("Your AI assistant generated")));
    }

    @Test
    void root_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
