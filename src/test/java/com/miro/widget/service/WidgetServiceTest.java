package com.miro.widget.service;

import com.miro.widget.model.Widget;
import com.miro.widget.model.WidgetAttributes;
import com.miro.widget.repository.WidgetContainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WidgetServiceTest {

    @Mock
    WidgetContainer widgetContainer;

    @InjectMocks
    WidgetService widgetService;

    @Test
    void shouldAddWidget() {
        WidgetAttributes attributes = new WidgetAttributes();
        Widget widget = Widget.builder().build();
        when(widgetContainer.saveAsForeground(any(Widget.class))).thenReturn(widget);
        Widget added = widgetService.addWidget(attributes);
        assertThat(added.getZIndex()).isZero();
        assertThat(added.getHeight()).isZero();
        verify(widgetContainer).saveAsForeground(any(Widget.class));
    }

    @Test
    void shouldAddWidgetWithZIndex() {
        WidgetAttributes attributes = new WidgetAttributes();
        attributes.setZIndex(3);
        Widget widget = Widget.builder().zIndex(3).build();
        when(widgetContainer.saveWithZIndex(any(Widget.class))).thenReturn(widget);
        Widget added = widgetService.addWidget(attributes);
        assertThat(added.getZIndex()).isEqualTo(3);
        assertThat(added.getHeight()).isZero();
        verify(widgetContainer).saveWithZIndex(any(Widget.class));
    }
}