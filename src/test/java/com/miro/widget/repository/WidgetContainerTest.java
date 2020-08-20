package com.miro.widget.repository;

import com.miro.widget.model.Widget;
import com.miro.widget.model.WidgetAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WidgetContainerTest {

    @Mock
    ZIndexContainer zIndexContainer;

    @InjectMocks
    WidgetContainer widgetContainer;

    @BeforeEach
    void setup() {
        widgetContainer.getWidgetStore().clear();
    }

    @Test
    void shouldSaveAsForeground() {
        Widget widget = Widget.builder().build();
        widgetContainer.saveAsForeground(widget);
        assertThat(widgetContainer.getWidgetStore()).hasSize(1).containsValue(widget);
        verify(zIndexContainer).addWidgetId(widget.getId());
    }

    @Test
    void shouldSaveWithZindex() {
        Widget widget = Widget.builder().zIndex(2).build();
        widgetContainer.saveWithZIndex(widget);
        assertThat(widgetContainer.getWidgetStore()).hasSize(1).containsValue(widget);
        verify(zIndexContainer).addWidgetId(widget.getId(), 2);
    }

    @Test
    void shouldSaveWithZindexAdjustExistingIndex() {
        Widget widget1 = Widget.builder().zIndex(2).build();
        Widget widget2 = Widget.builder().zIndex(3).build();
        Widget widget3 = Widget.builder().zIndex(4).build();
        Widget newWidget = Widget.builder().zIndex(2).build();
        widgetContainer.getWidgetStore().putAll(Map.of(widget1.getId(), widget1, widget2.getId(), widget2, widget3.getId(), widget3));
        when(zIndexContainer.reverseSortedTailZIndexes(2)).thenReturn(List.of(widget3.getId(), widget2.getId(), widget1.getId()));
        Widget result = widgetContainer.saveWithZIndex(newWidget);

        assertThat(widgetContainer.getWidgetStore()).hasSize(4);
        assertThat(widgetContainer.getWidgetStore().get(widget1.getId()).getZIndex()).isEqualTo(3);
        assertThat(widgetContainer.getWidgetStore().get(widget2.getId()).getZIndex()).isEqualTo(4);
        assertThat(widgetContainer.getWidgetStore().get(widget3.getId()).getZIndex()).isEqualTo(5);
        assertThat(widgetContainer.getWidgetStore().get(newWidget.getId()).getZIndex()).isEqualTo(2);
        assertThat(result).isEqualTo(newWidget);
        verify(zIndexContainer).addWidgetId(result.getId(), result.getZIndex());
    }

    @Test
    void shouldFindAll() {
        Widget widget1 = Widget.builder().build();
        Widget widget2 = Widget.builder().zIndex(2).build();
        when(zIndexContainer.findAll()).thenReturn(List.of(widget1.getId(), widget2.getId()));
        widgetContainer.saveAsForeground(widget1);
        widgetContainer.saveWithZIndex(widget2);
        List<Widget> result = widgetContainer.findAll();
        assertThat(widgetContainer.getWidgetStore()).hasSize(2);
        assertThat(result).hasSize(2).containsExactly(widget1, widget2);
        verify(zIndexContainer).findAll();
    }

    @Test
    void shouldFindAllWithPaging() {
        Widget widget1 = Widget.builder().build();
        Widget widget2 = Widget.builder().zIndex(2).build();
        when(zIndexContainer.findAll()).thenReturn(List.of(widget1.getId(), widget2.getId()));
        widgetContainer.saveAsForeground(widget1);
        widgetContainer.saveWithZIndex(widget2);
        Page<Widget> resultPage = widgetContainer.findAll(PageRequest.of(1, 1));
        assertThat(widgetContainer.getWidgetStore()).hasSize(2);
        assertThat(resultPage.getTotalElements()).isEqualTo(2);
        assertThat(resultPage.getTotalPages()).isEqualTo(2);
        assertThat(resultPage.getContent()).hasSize(1).containsExactly(widget2);
        verify(zIndexContainer).findAll();
    }

    @Test
    void shouldUpdateWidget() {
        Widget widget = Widget.builder().build();
        widgetContainer.saveAsForeground(widget);
        WidgetAttributes updateAttributes = new WidgetAttributes();
        updateAttributes.setZIndex(2);
        updateAttributes.setHeight(2);
        Widget updated = widgetContainer.update(widget.getId(), updateAttributes);
        assertThat(widgetContainer.findById(widget.getId())).isNotEmpty().isEqualTo(Optional.of(updated));
        assertThat(updated.getHeight()).isEqualTo(2);
        verify(zIndexContainer).removeWidgetId(0);
        verify(zIndexContainer).addWidgetId(widget.getId(), 2);
    }

    @Test
    void shouldUpdateWidgetByAdjustingOthers() {
        Widget widget = Widget.builder().zIndex(1).build();
        Widget widget1 = Widget.builder().zIndex(2).build();
        Widget widget2 = Widget.builder().zIndex(3).build();
        widgetContainer.getWidgetStore().putAll(Map.of(widget.getId(), widget, widget1.getId(), widget1, widget2.getId(), widget2));
        when(zIndexContainer.reverseSortedTailZIndexes(2)).thenReturn(List.of(widget2.getId(), widget1.getId()));
        WidgetAttributes updateAttributes = new WidgetAttributes();
        updateAttributes.setZIndex(2);
        updateAttributes.setHeight(2);
        Widget updated = widgetContainer.update(widget.getId(), updateAttributes);
        assertThat(widgetContainer.findById(widget.getId())).isNotEmpty().isEqualTo(Optional.of(updated));
        assertThat(widgetContainer.getWidgetStore()).hasSize(3);
        assertThat(widgetContainer.getWidgetStore().get(widget1.getId()).getZIndex()).isEqualTo(3);
        assertThat(widgetContainer.getWidgetStore().get(widget2.getId()).getZIndex()).isEqualTo(4);
        assertThat(updated.getHeight()).isEqualTo(2);
        assertThat(updated.getZIndex()).isEqualTo(2);
        verify(zIndexContainer).removeWidgetId(2);
        verify(zIndexContainer).addWidgetId(widget.getId(), 2);
    }
}