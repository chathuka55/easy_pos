/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author CJAY
 */
public class User {
    private int userID;
    private String name;
    private String username;
    private String password;
    private String contactNumber;
    private String address;
    private String role;
     private byte[] picture;
    

    // Getters and Setters
    public int getUserID() { return userID; }
    public void setUserID(int userID) { this.userID = userID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
     public byte[] getPicture() { return picture; }
    public void setPicture(byte[] picture) { this.picture = picture; }
    
     
}