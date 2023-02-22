package com.enn.ionic.js;

public class TokenBody {

    private String password;
    private String projectName;
    private String username;

    public TokenBody(String username,String password, String projectName) {
        this.password = password;
        this.projectName = projectName;
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
