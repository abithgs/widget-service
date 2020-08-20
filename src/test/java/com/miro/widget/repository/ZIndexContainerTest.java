package com.miro.widget.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ZIndexContainerTest {

    ZIndexContainer zIndexContainer;

    @BeforeEach
    void setUp() {
        zIndexContainer = new ZIndexContainer();
    }

    @Test
    void shouldCreateDefaultZIndexTracker() {
        assertThat(zIndexContainer.getZIndexTracker()).isEmpty();
    }

    @Test
    void shouldAddWidgetIdWithoutZIndex() {
        UUID widgetId1 = UUID.randomUUID();
        UUID widgetId2 = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId1);
        zIndexContainer.addWidgetId(widgetId2);
        assertThat(zIndexContainer.getZIndexTracker().keySet()).hasSize(2)
                .containsExactly(0, 1);
        assertThat(zIndexContainer.getZIndexTracker().values()).hasSize(2)
                .containsExactly(widgetId1, widgetId2);
    }

    @Test
    void shouldAddWidgetIdToForegroundZIndex() {
        UUID existingId = UUID.randomUUID();
        UUID widgetId = UUID.randomUUID();
        zIndexContainer.addWidgetId(existingId, 3);
        zIndexContainer.addWidgetId(widgetId);
        assertThat(zIndexContainer.getZIndexTracker()).hasSize(2).containsKeys(3, 4);
        assertThat(zIndexContainer.getZIndexTracker().values()).containsExactly(existingId, widgetId);
    }

    @Test
    void shouldAddWidgetIdWithZIndex() {
        UUID widgetId = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId, 3);
        assertThat(zIndexContainer.getZIndexTracker()).hasSize(1).containsKeys(3);
        assertThat(zIndexContainer.getZIndexTracker().values()).contains(widgetId);
    }

    @Test
    void shouldFailIfZIndexExists() {
        UUID widgetId = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId, 3);
        Assertions.assertThrows(IllegalStateException.class, () -> zIndexContainer.addWidgetId(widgetId, 3));
    }

    @Test
    void shouldGetWidgetsToShiftZIndex() {
        UUID widgetId1 = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId1, 3);
        UUID widgetId2 = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId2, 4);
        UUID widgetId3 = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId3, 5);
        assertThat(zIndexContainer.reverseSortedTailZIndexes(4)).hasSize(2).containsExactly(widgetId3, widgetId2);
    }

    @Test
    void shouldRemoveWidgetId() {
        UUID widgetId1 = UUID.randomUUID();
        UUID widgetId2 = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId1, 2);
        zIndexContainer.addWidgetId(widgetId2, 3);
        zIndexContainer.removeWidgetId(2);
        assertThat(zIndexContainer.getZIndexTracker()).hasSize(1).containsKeys(3);
        assertThat(zIndexContainer.getZIndexTracker().values()).containsExactly(widgetId2);
    }

    @Test
    void shouldReturnSortedList() {
        UUID widgetId0 = UUID.randomUUID();
        UUID widgetId11 = UUID.randomUUID();
        UUID widgetId12 = UUID.randomUUID();
        UUID widgetId2 = UUID.randomUUID();
        zIndexContainer.addWidgetId(widgetId0, 0);
        zIndexContainer.addWidgetId(widgetId11, 1);
        zIndexContainer.addWidgetId(widgetId12, 2);
        zIndexContainer.addWidgetId(widgetId2, -2);
        List<UUID> widgetIds = zIndexContainer.findAll();
        assertThat(zIndexContainer.getZIndexTracker()).hasSize(4);
        assertThat(zIndexContainer.getZIndexTracker().keySet()).containsExactly(-2, 0, 1, 2);
        assertThat(widgetIds).hasSize(4).containsExactly(widgetId2, widgetId0, widgetId11, widgetId12);
    }
}