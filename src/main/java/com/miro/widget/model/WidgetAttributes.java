package com.miro.widget.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;

@Getter
@Setter
@Slf4j
@NoArgsConstructor
public class WidgetAttributes {
    @JsonProperty(value = "x-index")
    private Integer xIndex;
    @JsonProperty(value = "y-index")
    private Integer yIndex;
    @JsonProperty(value = "z-index")
    private Integer zIndex;
    private Integer height;
    private Integer width;

    public WidgetAttributes(int height, int width) {
        this.height = height;
        this.width = width;
    }

    public static Widget toWidget(WidgetAttributes attributes) {
        Widget.WidgetBuilder widgetBuilder = Widget.builder();
        ofNullable(attributes.getXIndex()).ifPresent(widgetBuilder::xIndex);
        ofNullable(attributes.getYIndex()).ifPresent(widgetBuilder::yIndex);
        ofNullable(attributes.getZIndex()).ifPresent(widgetBuilder::zIndex);
        ofNullable(attributes.getHeight()).ifPresent(widgetBuilder::height);
        ofNullable(attributes.getWidth()).ifPresent(widgetBuilder::width);
        return widgetBuilder.build();
    }
}
