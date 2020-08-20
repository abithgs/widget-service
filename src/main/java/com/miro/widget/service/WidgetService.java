package com.miro.widget.service;

import com.miro.widget.exception.WidgetNotFoundException;
import com.miro.widget.model.Widget;
import com.miro.widget.model.WidgetAttributes;
import com.miro.widget.repository.WidgetContainer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
public class WidgetService {

    private final WidgetContainer widgetContainer;

    public WidgetService(WidgetContainer widgetContainer) {
        this.widgetContainer = widgetContainer;
    }

    /**
     * Adds a new Widget with provided attributes.
     *
     * @param widgetAttributes attributes
     * @return new {@link Widget}
     */
    public Widget addWidget(WidgetAttributes widgetAttributes) {
        Widget newWidget = WidgetAttributes.toWidget(widgetAttributes);
        return ofNullable(widgetAttributes.getZIndex()).map(index -> widgetContainer.saveWithZIndex(newWidget))
                .orElseGet(() -> widgetContainer.saveAsForeground(newWidget));
    }

    /**
     * Updates existing {@link Widget}. Throws {@link WidgetNotFoundException} if not found.
     *
     * @param widgetId         Unique WidgetId
     * @param widgetAttributes attributes
     * @return Updated {@link Widget}
     */
    public Widget updateWidget(UUID widgetId, WidgetAttributes widgetAttributes) {
        return widgetContainer.update(widgetId, widgetAttributes);
    }

    /**
     * Deletes a {@link Widget}. Throws {@link WidgetNotFoundException} if not found.
     *
     * @param widgetId Unique widgetId
     */
    public void removeWidget(UUID widgetId) {
        widgetContainer.delete(widgetId);
    }

    /**
     * Returns List of saved Widgets
     *
     * @return List of {@link Widget}'s
     */
    public List<Widget> findAllWidgets() {
        return widgetContainer.findAll();
    }

    /**
     * Returns List of saved Widgets with paging.
     *
     * @param page Page number, starting with 0
     * @param size Nos of elements
     * @return {@link Page} of {@link Widget}'s
     */
    public Page<Widget> findAllWidgets(int page, int size) {
        return widgetContainer.findAll(PageRequest.of(page, size));
    }

    /**
     * Returns existing {@link Widget}. Throws {@link WidgetNotFoundException} if not found.
     *
     * @param widgetId WidgetId
     * @return {@link Widget}
     */
    public Widget findWidget(UUID widgetId) {
        return widgetContainer.findById(widgetId).orElseThrow(() -> new WidgetNotFoundException("WidgetId not found : " + widgetId));
    }
}
