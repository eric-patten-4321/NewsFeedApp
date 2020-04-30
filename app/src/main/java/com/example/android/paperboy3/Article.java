package com.example.android.paperboy3;

public class Article {
    public String sectionId;
    public String sectionName;
    public String date;
    public String title;
    public String webUrl;
    public String author;
    public String image;

    public Article(String sectionId, String sectionName, String date, String title, String webUrl, String author, String image) {
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.date = date;
        this.title = title;
        this.webUrl = webUrl;
        this.author = author;
        this.image = image;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getImage() {
        return image;
    }
}
