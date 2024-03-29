//-----------------------------------com.example.getkracking.API.model.Credentials.java-----------------------------------
package com.example.getkracking.API.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CredentialsModel {

    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;

    /**
     * No args constructor for use in serialization
     *
     */
    public CredentialsModel() {
    }

    /**
     *
     * @param password
     * @param username
     */
    public CredentialsModel(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}