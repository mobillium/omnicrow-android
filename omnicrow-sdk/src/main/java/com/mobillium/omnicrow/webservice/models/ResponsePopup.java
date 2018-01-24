package com.mobillium.omnicrow.webservice.models;

/**
 * Created by emretekin on 29/12/16.
 */

public class ResponsePopup {

    private int id;
    private String title;
    private String content;
    private String button;
    private String uri;
    private String extra_text;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getExtra_text() {
        return extra_text;
    }

    public void setExtra_text(String extra_text) {
        this.extra_text = extra_text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
