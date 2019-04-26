package com.example.inspirationalr;

import java.io.Serializable;

public class RewardsDetails implements Serializable {
    private String studentID;
    private String UserName;

    private String historyDate;
    private String rewardProvider;              // name
    private String points;
    private String comments;

    public RewardsDetails(String studentID, String userName, String historyDate,
                          String rewardProvider, String points, String comments) {
        this.studentID = studentID;
        UserName = userName;
        this.historyDate = historyDate;
        this.rewardProvider = rewardProvider;
        this.points = points;
        this.comments = comments;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public String getRewardProvider() {
        return rewardProvider;
    }

    public void setRewardProvider(String rewardProvider) {
        this.rewardProvider = rewardProvider;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
