package com.emeraldElves.alcohollabelproject.Data;

import com.emeraldElves.alcohollabelproject.AppState;
import com.emeraldElves.alcohollabelproject.Log;

import javax.naming.ServiceUnavailableException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Harry on 4/9/2017.
 */
public class Storage {
    private AlcoholDatabase alcoholDB;
    private AuthenticatedUsersDatabase usersDB;
    private Database db;

    private Storage() {
        if (!AppState.getInstance().isInTestingMode)
            db = initDatabase("ttbDB");
        else
            db = initDatabase("testDB");
        alcoholDB = new AlcoholDatabase(db);
        usersDB = new AuthenticatedUsersDatabase(db);
    }

    private Database initDatabase(String dbName) {
        Database database = new Database(dbName);
        database.connect();
        try {
            database.createTable("TTBAgentLogin", new Database.TableField("username", "VARCHAR (255) UNIQUE NOT NULL"),
                    new Database.TableField("password", "VARCHAR (255) NOT NULL"));
            Log.console("Created new TTBAgentLogin table");
        } catch (SQLException e) {
            Log.console("Used existing TTBAgentLogin table");
        }

        try {
            database.createTable("ApplicantLogin", new Database.TableField("username", "VARCHAR (255) UNIQUE NOT NULL"),
                    new Database.TableField("password", "VARCHAR (255) NOT NULL"));
            Log.console("Created new ApplicantLogin table");
        } catch (SQLException e) {
            Log.console("Used existing ApplicantLogin table");
        }

        try {
            database.createTable("SubmittedApplications", new Database.TableField("applicationID", "INTEGER UNIQUE NOT NULL"),
                    new Database.TableField("applicantID", "INTEGER NOT NULL"),
                    new Database.TableField("status", "INTEGER NOT NULL"),
                    new Database.TableField("statusMsg", "VARCHAR (10000) NOT NULL"),
                    new Database.TableField("submissionTime", "BIGINT NOT NULL"),
                    new Database.TableField("expirationDate", "BIGINT"),
                    new Database.TableField("agentName", "VARCHAR (255)"),
                    new Database.TableField("approvalDate", "BIGINT"),
                    new Database.TableField("TTBUsername", "VARCHAR (255)"),
                    new Database.TableField("submitterUsername", "VARCHAR (255)"),
                    new Database.TableField("extraInfo", "VARCHAR (1000)"),
                    new Database.TableField("labelApproval", "BOOLEAN"),
                    new Database.TableField("stateOnly", "VARCHAR (2)"),
                    new Database.TableField("bottleCapacity", "INTEGER"),
                    new Database.TableField("imageURL", "VARCHAR (255)"));
            Log.console("Created new SubmittedApplications table");
        } catch (SQLException e) {
            Log.console("Used existing SubmittedApplications table");
        }

        try {
            database.createTable("ManufacturerInfo", new Database.TableField("applicationID", "INTEGER UNIQUE NOT NULL"),
                    new Database.TableField("authorizedName", "VARCHAR (255) NOT NULL"),
                    new Database.TableField("physicalAddress", "VARCHAR (255) NOT NULL"),
                    new Database.TableField("company", "VARCHAR (10000) NOT NULL"),
                    new Database.TableField("representativeID", "INTEGER NOT NULL"),
                    new Database.TableField("permitNum", "INTEGER NOT NULL"),
                    new Database.TableField("phoneNum", "VARCHAR (255) NOT NULL"), //check with kyle
                    new Database.TableField("emailAddress", "VARCHAR (255) NOT NULL"));
            Log.console("Created new ManufacturerInfo table");
        } catch (SQLException e) {
            Log.console("Used existing ManufacturerInfo table");
        }

        try {
            database.createTable("AlcoholInfo", new Database.TableField("applicationID", "INTEGER UNIQUE NOT NULL"),
                    new Database.TableField("alcoholContent", "INTEGER NOT NULL"),
                    new Database.TableField("fancifulName", "VARCHAR (255)"),
                    new Database.TableField("brandName", "VARCHAR (10000) NOT NULL"),
                    new Database.TableField("origin", "INTEGER NOT NULL"),
                    new Database.TableField("type", "INTEGER NOT NULL"),
                    new Database.TableField("formula", "VARCHAR (255) NOT NULL"),
                    new Database.TableField("serialNumber", "VARCHAR (255) NOT NULL"),
                    new Database.TableField("pH", "REAL"),
                    new Database.TableField("vintageYear", "INTEGER"),
                    new Database.TableField("varietals", "VARCHAR (255)"),
                    new Database.TableField("wineAppellation", "VARCHAR (255)"));
            Log.console("Created new AlcoholInfo table");
        } catch (SQLException e) {
            Log.console("Used existing AlcoholInfo table");
        }

        return database;
    }


    public static Storage getInstance() {
        return StorageHolder.instance;
    }


    private static class StorageHolder {
        public static final Storage instance = new Storage();
    }


    public boolean submitApplication(SubmittedApplication application, String username) {
        return alcoholDB.submitApplication(application, username);
    }

    public boolean approveApplication(SubmittedApplication application, String agentName, Date expirationDate) {
        return alcoholDB.approveApplication(application, agentName, expirationDate);
    }

    public boolean rejectApplication(SubmittedApplication application, String reason) {
        return alcoholDB.rejectApplication(application, reason);
    }

    public boolean createUser(UserType usertype, String username, String password) {
        if (usertype == UserType.TTBAGENT) {
            return db.insert("'" + username + "', '" + password + "'", "TTBAgentLogin");
        } else {
            return db.insert("'" + username + "', '" + password + "'", "ApplicantLogin");
        }
    }

    public boolean isValidUser(UserType usertype, String username, String password) {
        if (usertype == UserType.TTBAGENT) {
            return usersDB.isValidTTBAgent(username, password);
        } else if (usertype == UserType.APPLICANT) {
            return usersDB.isValidApplicant(username, password);
        }
        return false;
    }

    public List<SubmittedApplication> getRecentlyApprovedApplications(int numApps) {
        return alcoholDB.getMostRecentApproved(numApps);
    }

    public List<SubmittedApplication> getApprovedApplications() {
        return alcoholDB.getApproved();
    }

    public List<SubmittedApplication> getApplicationsByBrandName(String brandName) {
        return alcoholDB.searchByBrandName(brandName);
    }

    public List<SubmittedApplication> getApplicationsByName(String name) {
        return alcoholDB.searchByName(name);
    }

    public List<SubmittedApplication> getApplicationsByFancifulName(String fancifulName) {
        return alcoholDB.searchByFancifulName(fancifulName);
    }

    public List<SubmittedApplication> getApplicationsByApplicant(String username) {
        return alcoholDB.getApplicationsByApplicantUsername(username);
    }

    public List<SubmittedApplication> getAssignedApplications(String agentName) {
        return alcoholDB.getAssignedApplications(agentName);
    }


}