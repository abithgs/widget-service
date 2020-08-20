package com.miro.widget.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Builder
@Setter
@ToString
public class Widget {

    @Builder.Default
    private UUID id = UUID.randomUUID();
    @JsonProperty(value = "x-index")
    private int xIndex;
    @JsonProperty(value = "y-index")
    private int yIndex;
    @JsonProperty(value = "z-index")
    private int zIndex;
    private int height;
    private int width;
    @Builder.Default
    private ZonedDateTime lastUpdatedAt = ZonedDateTime.now();

    public void incrementZindex() {
        zIndex++;
    }
}
