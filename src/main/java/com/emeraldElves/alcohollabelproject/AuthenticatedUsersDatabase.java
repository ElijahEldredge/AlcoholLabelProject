package com.emeraldElves.alcohollabelproject;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Dan on 3/31/2017.
 */
public class AuthenticatedUsersDatabase {
    private Database db;

    /**
     * Creates an AuthenticatedUsersDatabase
     *
     * @param db the main database that contains the data
     */
    public AuthenticatedUsersDatabase(Database db) {
        this.db = db;
    }

    /**
     * Checks if TTB agent login is valid.
     *
     * @param userName The username of the TTB agent
     * @param password The password of the TTB agent
     * @return True if the TTB agent is valid, False otherwise
     */
    public boolean isValidTTBAgent(String userName, String password) {
        ResultSet results = db.select("*", "TTBAgentLogin", "username = '" + userName +
                "' AND  password = '" + password + "'");
        if (results == null)
            return false;
        try {
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if Applicant login is valid.
     *
     * @param userName The username of the applicant
     * @param password The password of the applicant
     * @return True if the applicant login is valid, False otherwise
     */
    public boolean isValidApplicant(String userName, String password) {
        ResultSet results = db.select("*", "ApplicantLogin", "username = '" + userName +
                "' AND  password = '" + password + "'");
        if (results == null)
            return false;
        try {
            return results.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}