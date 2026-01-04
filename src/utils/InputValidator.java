/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author CJAY
 */
public class InputValidator {

    /**
     * Validates a username.
     *
     * @param username The username to validate.
     * @return True if the username is valid, otherwise false.
     */
    public static boolean isValidUsername(String username) {
        return username != null && !username.isEmpty() && username.length() >= 4;
    }

    /**
     * Validates a password.
     *
     * @param password The password to validate.
     * @return True if the password is valid, otherwise false.
     */
    public static boolean isValidPassword(String password) {
        return password != null && !password.isEmpty() && password.length() >= 6;
    }
}
