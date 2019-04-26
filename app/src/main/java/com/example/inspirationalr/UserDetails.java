package com.example.inspirationalr;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDetails implements Serializable, Comparable<UserDetails> {
    private String studentID;
    private String firstname;
    private String lastname;
    private String username;
    private String department;
    private String story;
    private String position;
    private String password;
    private int pointstoaward;
    private boolean admin;
    private String location;
    private String image;
    private List<RewardsDetails> rewards = new ArrayList<>();
    private int points;


    public UserDetails(String studentID, String firstname, String lastname, String username,
                       String department, String story, String position, String password,
                       int pointstoaward, boolean admin, String location, String image,
                       List<RewardsDetails> rewards) {
        if(!studentID.equalsIgnoreCase("A20413890"))
            this.studentID = "A20413890";
        else
            this.studentID = studentID;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.department = department;
        this.story = story;
        this.position = position;
        this.password = password;
        this.pointstoaward = pointstoaward;
        this.admin = admin;
        this.location = location;
        this.image = image;
        if(rewards == null || rewards.size() == 0 || rewards.isEmpty()) {

        } else {
            this.rewards = rewards;
        }
        setPoints();

    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPointstoaward() {
        return pointstoaward;
    }

    public void setPointstoaward(int pointstoaward) {
        this.pointstoaward = pointstoaward;
    }

    public boolean getAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<RewardsDetails> getRewards() {
        return rewards;
    }

    public void setRewards(List<RewardsDetails> rewards) {
        this.rewards = rewards;
    }


    public int getPoints() {
        int points = 0;
        for(RewardsDetails r : this.rewards){
            points += Integer.parseInt(r.getPoints());
        }
        return points;
    }

    public void setPoints() {
        int points = 0;
        for(RewardsDetails r : this.rewards){
            points += Integer.parseInt(r.getPoints());
        }
        this.points = points;
    }

    @Override
    public int compareTo(UserDetails user) {

        if (this.points > user.points)
            return -1;
        if (this.points < user.points)
            return 1;
        else
            return 0;
    }
}