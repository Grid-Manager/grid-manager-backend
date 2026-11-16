package com.Lino.grid_manager_back.infrastructure.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.Lino.grid_manager_back.category.dto.CategoryResponse;
import com.Lino.grid_manager_back.category.dto.CreateCategoryRequest;
import com.Lino.grid_manager_back.category.service.CategoryService;
import com.Lino.grid_manager_back.infrastructure.dto.PagedResponse;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void allowsPublicCategoryReads() throws Exception {
        when(categoryService.findAll(any())).thenReturn(new PagedResponse<>(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk());
    }

    @Test
    void deniesAnonymousCategoryWrites() throws Exception {
        mockMvc.perform(post("/api/v1/categories").contentType("application/json")
                .content("{}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deniesUserCategoryWrites() throws Exception {
        mockMvc.perform(post("/api/v1/categories").contentType("application/json")
                .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void allowsAdminCategoryWrites() throws Exception {
        when(categoryService.create(any(CreateCategoryRequest.class))).thenReturn(
                new CategoryResponse(1L, "Formula One", "F1", "Hybrid", "Single-seater", "Pirelli",
                        "FIA", 1000, 1950, "F1-DATE-DRIVER"));

        mockMvc.perform(post("/api/v1/categories").contentType("application/json")
                .content("""
                        {"name":"Formula One","acronym":"F1","propulsionType":"Hybrid",
                        "vehicleType":"Single-seater","tireSupplier":"Pirelli","governingBody":"FIA",
                        "horsePower":1000,"foundingYear":1950,"licensePattern":"F1-DATE-DRIVER"}
                        """))
                .andExpect(status().isCreated());
    }
}
