package com.miro.widget.controller;

import com.miro.widget.model.Widget;
import com.miro.widget.model.WidgetAttributes;
import com.miro.widget.service.WidgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@RestController
@RequestMapping(value = "/api/widgets")
@RequiredArgsConstructor
public class WidgetController {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 500;
    private final WidgetService widgetService;

    @PostMapping
    public ResponseEntity<Widget> addWidget(@RequestBody WidgetAttributes attributes) {
        return new ResponseEntity<>(widgetService.addWidget(attributes), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Widget> updateWidget(@PathVariable UUID id, @RequestBody WidgetAttributes attributes) {
        return ResponseEntity.ok(widgetService.updateWidget(id, attributes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Widget> findWidget(@PathVariable UUID id) {
        return ResponseEntity.ok(widgetService.findWidget(id));
    }

    @GetMapping
    public ResponseEntity<List<Widget>> findAllWidgets() {
        return ResponseEntity.ok(widgetService.findAllWidgets());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteWidget(@PathVariable UUID id) {
        widgetService.removeWidget(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<Widget>> findAllWidgets(@RequestParam(required = false) Integer page
            , @RequestParam(required = false) Integer size) {
        if (Objects.nonNull(size) && size > MAX_SIZE) {
            return ResponseEntity.badRequest().build();
        }
        Page<Widget> widgetPage = widgetService.findAllWidgets(ofNullable(page).orElse(DEFAULT_PAGE),
                ofNullable(size).orElse(DEFAULT_SIZE));
        return ResponseEntity.ok(widgetPage);
    }
}
