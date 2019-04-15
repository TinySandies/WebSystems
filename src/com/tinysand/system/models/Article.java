package com.tinysand.system.models;

import com.tinysand.system.controllers.NotNull;

import java.sql.Timestamp;

@SuppressWarnings("unused")
public class Article {
    public Article(String articleTitle, String articleContent,
                   String articleLabel, String articleFolder,
                   String description, String publisher) {
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
        this.articleFolder = articleFolder;
        this.description = description;
        this.publisher = publisher;
        this.articleLabel = articleLabel;
    }

    @Override
    public String toString() {
        return "Article{" +
                "lastModified=" + lastModified +
                ", postTime=" + postTime +
                ", publisher=" + publisher +
                ", articleFolder=" + articleFolder +
                ", viewTimes=" + viewTimes +
                ", wordCounter=" + wordCounter +
                ", recommend=" + recommend +
                ", articleTitle='" + articleTitle + '\'' +
                ", articleContent='" + articleContent + '\'' +
                ", description='" + description + '\'' +
                ", titleImage='" + titleImage + '\'' +
                ", articleLabel='" + articleLabel + '\'' +
                ", id=" + id +
                ", top=" + top +
                ", essay=" + essay +
                ", publish=" + publish +
                '}';
    }

    @NotNull
    private String articleTitle;
    @NotNull
    private String articleContent;
    @NotNull
    private String articleLabel;
    @NotNull
    private String articleFolder;
    @NotNull
    private String description;
    @NotNull
    private String publisher;
    private Timestamp lastModified;
    private Timestamp postTime;
    private Integer viewTimes;
    private Integer wordCounter;
    private Integer recommend;
    private String titleImage;
    private Integer id;
    private Boolean essay;



    private Boolean top;
    private Boolean publish;

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public Timestamp getPostTime() {
        return postTime;
    }

    public void setPostTime(Timestamp postTime) {
        this.postTime = postTime;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getArticleFolder() {
        return articleFolder;
    }

    public void setArticleFolder(String articleFolder) {
        this.articleFolder = articleFolder;
    }

    public Integer getViewTimes() {
        return viewTimes;
    }

    public void setViewTimes(Integer viewTimes) {
        this.viewTimes = viewTimes;
    }

    public Integer getWordCounter() {
        return wordCounter;
    }

    public void setWordCounter(Integer wordCounter) {
        this.wordCounter = wordCounter;
    }

    public Integer getRecommend() {
        return recommend;
    }

    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitleImage() {
        return titleImage;
    }

    public void setTitleImage(String titleImage) {
        this.titleImage = titleImage;
    }

    public String getArticleLabel() {
        return articleLabel;
    }

    public void setArticleLabel(String articleLabel) {
        this.articleLabel = articleLabel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEssay() {
        return essay;
    }

    public void setEssay(Boolean essay) {
        this.essay = essay;
    }

    public Boolean getTop() {
        return top;
    }

    public void setTop(Boolean top) {
        this.top = top;
    }

    public Boolean getPublish() {
        return publish;
    }

    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public Article() {
    }

    public Article(Timestamp lastModified, Timestamp postTime,
                   String articleFolder, String publisher,
                   Integer viewTimes, Integer wordCounter,
                   Integer recommend, String articleTitle,
                   String articleContent, String description,
                   String titleImage, String articleLabel,
                   Integer id, Boolean top, Boolean publish,
                   Boolean essay) {
        this.lastModified = lastModified;
        this.postTime = postTime;
        this.articleFolder = articleFolder;
        this.publisher = publisher;
        this.viewTimes = viewTimes;
        this.wordCounter = wordCounter;
        this.recommend = recommend;
        this.articleTitle = articleTitle;
        this.articleContent = articleContent;
        this.description = description;
        this.titleImage = titleImage;
        this.articleLabel = articleLabel;
        this.id = id;
        this.essay = essay;
        this.top = top;
        this.publish = publish;
    }
}
