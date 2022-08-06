package com.example.beerbrary.models;

public class ReviewModel {
    private boolean showBeer;
    private String review;
    private int beerId;
    private String beer;
    private String userId;
    private String user;
    private int score;

    public void setShowBeer(boolean val) {
        this.showBeer = val;
    }
    public boolean getShowBeer() {
        return this.showBeer;
    }

    public void setReview(String text) {
        this.review = text;
    }
    public String getReview(){
        return this.review;
    }

    public void setBeerId(int val) {
        this.beerId = val;
    }
    public int getBeerId() {
        return this.beerId;
    }

    public void setBeer(String val) {
        this.beer = val;
    }
    public String getBeer() {
            return this.beer;
    }

    public void setUserId(String val) {
        this.userId = val;
    }
    public String getUserId(){
        return this.userId;
    }

    public void setUser(String val) {
        this.user = val;
    }
    public String getUser() {
        return this.user;
    }

    public void setScore(int val) {
        this.score = val;
    }
    public int getScore(){
        return this.score;
    }
}
