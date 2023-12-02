package com.eclipse.mxd.model;

public class ContentRendomResponse {

    private Integer id;
    private String title;
    private String text;


    public ContentRendomResponse() {
    }

    public ContentRendomResponse(Integer id, String title, String text) {
        this.id = id;
        this.title = title;
        this.text = text;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "ContentRendomResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
