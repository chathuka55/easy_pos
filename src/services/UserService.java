/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

/**
 *
 * @author CJAY
 */
import dao.UserDAO;
import java.sql.SQLException;
import models.User;

public class UserService {

    private static final UserDAO userDAO = new UserDAO();

    /**
     * Authenticates a user based on username and password.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     * @return The authenticated User object, or null if authentication fails.
     */
   

   public static User authenticate(String username, String password) throws SQLException {
     return userDAO.getUserByCredentials(username, password);
    }
}

