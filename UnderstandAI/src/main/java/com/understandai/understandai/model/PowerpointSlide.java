package com.understandai.understandai.model;
import lombok.Data;

@Data
public class PowerpointSlide {
    private String title;
    private String content;
    private String subtitle;

    public PowerpointSlide(String title, String content, String subtitle) {
        this.title = title;
        this.content = content;
        this.subtitle = subtitle;
    }
}
