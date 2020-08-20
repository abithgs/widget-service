package com.miro.widget.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miro.widget.model.Widget;
import com.miro.widget.model.WidgetAttributes;
import com.miro.widget.service.WidgetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class WidgetControllerIntTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WidgetService widgetService;

    @Test
    void shouldAddNewWidget() throws Exception {
        WidgetAttributes attributes = new WidgetAttributes(10, 10);
        attributes.setZIndex(3);
        mockMvc.perform(post("/api/widgets")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(attributes)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.height").value(10))
                .andExpect(jsonPath("$.width").value(10))
                .andExpect(jsonPath("$.z-index").value(3));
    }

    @Test
    void shouldUpdateWidget() throws Exception {
        WidgetAttributes attributes = new WidgetAttributes(10, 10);
        Widget widget = widgetService.addWidget(attributes);
        attributes = new WidgetAttributes(20, 20);
        mockMvc.perform(put("/api/widgets/" + widget.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(attributes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.height").value(20))
                .andExpect(jsonPath("$.width").value(20));
    }

    @Test
    void shouldDeleteWidget() throws Exception {
        WidgetAttributes attributes = new WidgetAttributes(10, 10);
        Widget widget = widgetService.addWidget(attributes);
        mockMvc.perform(delete("/api/widgets/" + widget.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(attributes)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFindWidget() throws Exception {
        WidgetAttributes attributes = new WidgetAttributes(10, 10);
        Widget widget = widgetService.addWidget(attributes);
        mockMvc.perform(get("/api/widgets/" + widget.getId())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.height").value(10))
                .andExpect(jsonPath("$.width").value(10));
    }

    @Test
    void shouldNotFindWidget() throws Exception {
        UUID widgetId = UUID.randomUUID();
        mockMvc.perform(get("/api/widgets/" + widgetId)
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("WidgetId not found : " + widgetId));
    }

    @Test
    void shouldListWidgets() throws Exception {
        widgetService.addWidget(new WidgetAttributes(10, 10));
        Widget widget = widgetService.addWidget(new WidgetAttributes(20, 20));
        widget.setZIndex(3);
        mockMvc.perform(get("/api/widgets/")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("@[0].height").value(10))
                .andExpect(jsonPath("@[1].height").value(20));
    }

}