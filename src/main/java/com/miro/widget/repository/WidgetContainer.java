package com.miro.widget.repository;

import com.google.common.annotations.VisibleForTesting;
import com.miro.widget.exception.WidgetNotFoundException;
import com.miro.widget.model.Widget;
import com.miro.widget.model.WidgetAttributes;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * Saves Widgets in {@link ConcurrentHashMap} with WidgetId as Keys.
 */
@Repository
public class WidgetContainer {

    @Getter(onMethod = @__(@VisibleForTesting), value = AccessLevel.PACKAGE)
    private final ConcurrentHashMap<UUID, Widget> widgetStore;
    private final ZIndexContainer zIndexContainer;

    public WidgetContainer(ZIndexContainer zIndexContainer) {
        this.zIndexContainer = zIndexContainer;
        widgetStore = new ConcurrentHashMap<>();
    }

    /**
     * Saves widget to Foreground of the plane.
     *
     * @param widget {@link Widget}
     * @return Saved {@link Widget}
     */
    public Widget saveAsForeground(Widget widget) {
        return widgetStore.computeIfAbsent(widget.getId(), id -> {
            widget.setZIndex(zIndexContainer.addWidgetId(id));
            return widget;
        });
    }

    /**
     * Saves widget to provided z-index.
     *
     * @param widget {@link Widget}
     * @return Saved {@link Widget}
     */
    public Widget saveWithZIndex(Widget widget) {
        adjustExistingWidgets(widget.getZIndex());
        return widgetStore.computeIfAbsent(widget.getId(), id -> {
            zIndexContainer.addWidgetId(id, widget.getZIndex());
            return widget;
        });
    }

    private void adjustExistingWidgets(int zIndex) {
        synchronized (widgetStore) {
            zIndexContainer.reverseSortedTailZIndexes(zIndex).stream()
                    .map(widgetStore::get)
                    .forEach(this::updateAdjustedWidgets);
        }
    }

    private void updateAdjustedWidgets(Widget widget) {
        zIndexContainer.removeWidgetId(widget.getZIndex());
        widget.incrementZindex();
        zIndexContainer.addWidgetId(widget.getId(), widget.getZIndex());
        widgetStore.put(widget.getId(), widget);
    }

    /**
     * Find {@link Widget} by ID.
     *
     * @return Optional of {@link Widget}
     */
    public Optional<Widget> findById(UUID widgetId) {
        return ofNullable(widgetStore.get(widgetId));
    }

    /**
     * Find all widgets.
     *
     * @return List of WidgetId's
     */
    public List<Widget> findAll() {
        return zIndexContainer.findAll().stream()
                .map(widgetStore::get)
                .collect(Collectors.toList());
    }

    /**
     * Updates existing Widget.
     *
     * @param widgetId         widget Uuique D
     * @param widgetAttributes attributes.
     * @return {@link Widget}
     */
    public Widget update(UUID widgetId, WidgetAttributes widgetAttributes) {
        Widget updated = widgetStore.computeIfPresent(widgetId,
                (id, widget) -> updateWidgetAttributes(widgetId, widgetAttributes, widget));
        if (Objects.isNull(updated)) {
            throw new WidgetNotFoundException("WidgetId not found : " + widgetId);
        }
        return updated;
    }

    private Widget updateWidgetAttributes(UUID widgetId, WidgetAttributes widgetAttributes, Widget widget) {
        ofNullable(widgetAttributes.getHeight()).ifPresent(widget::setHeight);
        ofNullable(widgetAttributes.getWidth()).ifPresent(widget::setWidth);
        ofNullable(widgetAttributes.getXIndex()).ifPresent(widget::setXIndex);
        ofNullable(widgetAttributes.getYIndex()).ifPresent(widget::setYIndex);
        ofNullable(widgetAttributes.getZIndex()).ifPresent(index -> {
            adjustExistingWidgets(index);
            zIndexContainer.removeWidgetId(widget.getZIndex());
            zIndexContainer.addWidgetId(widgetId, index);
            widget.setZIndex(index);
        });
        widget.setLastUpdatedAt(ZonedDateTime.now());
        return widget;
    }

    public Page<Widget> findAll(Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), widgetStore.size());
        return new PageImpl<>(findAll().subList(start, end), pageable, widgetStore.size());
    }

    public void delete(UUID widgetId) {
        Widget widget = widgetStore.remove(widgetId);
        if (Objects.isNull(widget)) {
            throw new WidgetNotFoundException("WidgetId not found : " + widgetId);
        }
        zIndexContainer.removeWidgetId(widget.getZIndex());
    }

}
