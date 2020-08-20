package com.miro.widget.repository;

import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.springframework.data.mapping.Alias.ofNullable;

/**
 * Tracks z-index and WidgetId's at the position. Saves in {@link ConcurrentSkipListMap} with z-index as key and value
 * as a Set of WidgetId's.
 */
@Repository
public class ZIndexContainer {

    @Getter(onMethod = @__(@VisibleForTesting), value = AccessLevel.PACKAGE)
    private final ConcurrentSkipListMap<Integer, UUID> zIndexTracker;

    public ZIndexContainer() {
        this.zIndexTracker = new ConcurrentSkipListMap<>(Comparator.comparingInt(Integer::intValue));
    }

    /**
     * Adds widgetId to foreground zIndex
     *
     * @param widgetId Unique WidgetId
     * @return foreground zIndex
     */
    public int addWidgetId(UUID widgetId) {
        Integer foregroundIndex;
        try {
            foregroundIndex = zIndexTracker.lastKey();
            foregroundIndex++;
        } catch (NoSuchElementException exc) {
            foregroundIndex = 0;
        }
        zIndexTracker.put(foregroundIndex, widgetId);
        return foregroundIndex;
    }

    /**
     * Adds widgetId at zIndex position
     *
     * @param widgetId widgetId
     * @param zIndex   zIndex to be added
     * @return zIndex
     * @throws IllegalStateException if existing zIndex is present
     */
    public int addWidgetId(UUID widgetId, int zIndex) {
        if (ofNullable(zIndexTracker.putIfAbsent(zIndex, widgetId)).isPresent()) {
            throw new IllegalStateException("zIndex already exists");
        }
        return zIndex;
    }

    /**
     * Returns reverse sorted Widgets with zIndex greater than or equal to zIndex
     *
     * @param zIndex zIndex
     * @return List of reverse sorted widgetIds
     */
    public List<UUID> reverseSortedTailZIndexes(int zIndex) {
        if (!zIndexTracker.containsKey(zIndex)) {
            return emptyList();
        }
        return zIndexTracker.tailMap(zIndex).descendingMap().values().stream().collect(toUnmodifiableList());
    }

    public void removeWidgetId(int zIndex) {
        if (!zIndexTracker.containsKey(zIndex)) {
            throw new IllegalArgumentException("zIndex not found");
        }
        zIndexTracker.remove(zIndex);
    }

    public List<UUID> findAll() {
        return zIndexTracker.values().stream().collect(toUnmodifiableList());
    }
}
