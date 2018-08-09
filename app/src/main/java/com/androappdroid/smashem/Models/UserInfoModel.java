package com.androappdroid.smashem.Models;

public class UserInfoModel {

    private String userName;
    private String emailId;
    private String userId;
    private int topScore;

    public String getUserName() {
        return userName;
    }

    public int getTopScore() {
        return topScore;
    }

    public void setTopScore(int topScore) {
        this.topScore = topScore;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
