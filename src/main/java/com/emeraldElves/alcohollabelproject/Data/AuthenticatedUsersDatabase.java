package com.emeraldElves.alcohollabelproject.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        ResultSet results = db.select("*", "TTBAgentLogin", "email = '" + userName +
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

    public boolean createUser(PotentialUser user){
        if (user.getUserType() == UserType.TTBAGENT) {
            return db.insert("'" + user.getName()
                            + "', '" + user.getPassword() + "', "
                            + user.getRepresentativeID() + ", "
                            + user.getPermitNum() + ", '"
                            + user.getAddress() + "', '"
                            + user.getPhoneNumber().getPhoneNumber() + "', '"
                            + user.getEmail().getEmailAddress() + "'"
                    , "TTBAgentLogin");
        } else { // type is Applicant
            return db.insert("'" + user.getName()
                            + "', '" + user.getPassword() + "', "
                            + user.getRepresentativeID() + ", "
                            + user.getPermitNum() + ", '"
                            + user.getAddress() + "', '"
                            + user.getPhoneNumber().getPhoneNumber() + "', '"
                            + user.getEmail().getEmailAddress() + "'"
                    , "ApplicantLogin");
        }
    }



    public int getRepresentativeID(String username) {
        ResultSet resultSet = db.select("representativeID", "ApplicantLogin", "username = '" + username + "'");
        try {
            if (resultSet.next()) {
                return resultSet.getInt("representativeID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<String> getAllAgents() {
        ResultSet resultSet = db.select("email", "TTBAgentLogin");
        List<String> agents = new ArrayList<>();
        try {
            while (resultSet.next()) {
                agents.add(resultSet.getString("email"));
            }
            return agents;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    /**
     * Checks if Applicant login is valid.
     *
     * @param email The email of the applicant
     * @param password The password of the applicant
     * @return True if the applicant login is valid, False otherwise
     */
    public boolean isValidApplicant(String email, String password) {
        ResultSet results = db.select("*", "ApplicantLogin", "email = '" + email +
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
    public boolean isValidSuperUser(String userName, String password){
        if(userName.equals("JoseWong")) {
            if (password.equals("password")) {
                return true;
            }
        }
        else
            return false;
        return false;
    }

    public boolean isValidAccount(String userName, String password){
        return isValidApplicant(userName, password) || isValidTTBAgent(userName, password);
    }

    public UserType getAccountType(String userName, String password){
        if(isValidTTBAgent(userName, password))
            return UserType.TTBAGENT;
        if(isValidApplicant(userName, password))
            return UserType.APPLICANT;
        return UserType.BASIC;
    }

    /**
     * Adds a potential user to database to be accepted/rejected by superagent
     * @param user the potential user to bee added
     * @return Whether or not it added the potential user to database successfully
     */
    public boolean addPotentialUser(PotentialUser user){
        boolean worked;
        // I dont think i need to check database, because you cant update if its already in queue
        try{

            worked = db.insert("'" + user.getName()
                            + "', '" + user.getPassword() + "', "
                            + user.getUserType().getValue() + ", "
                            + user.getRepresentativeID() + ", "
                            + user.getPermitNum() + ", '"
                            + user.getAddress() + "', '"
                            + user.getPhoneNumber().getPhoneNumber() + "', '"
                            + user.getEmail().getEmailAddress() + "', "
                            + user.getDate().getTime()
                    , "NewApplicant");
            /*
            worked = db.insert("'" + user.getName() + "', '"
                    + user.getPassword() + "', "
                    + user.getUserType().getValue(), "NewApplicant");
            */
            if(!worked){ throw new SQLException("Failed to add user");}
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;//dont think this is the right way to do it
        }

        return true;
    }

    /**
     *
     * @return all potential users in the NewApplicant table
     */
    public List<PotentialUser> getPotentialUsers(){
        ResultSet resultSet = db.select("*", "NewApplicant");
        List<PotentialUser> users = new ArrayList<>();
        try {
            while (resultSet.next()) {

                //Adding all stuff from database to new PotentialUser object
                String name = resultSet.getString("name");
                //String username = (resultSet.getString("email"));
                String password = resultSet.getString("password");
                int usertype = resultSet.getInt("type");
                UserType useType = UserType.fromInt(usertype);
                int representativeID = resultSet.getInt("representativeID");
                String emailString = resultSet.getString("email");
                EmailAddress email = new EmailAddress(emailString);
                String phoneNumberString = resultSet.getString("phoneNumber");
                PhoneNumber phoneNumber = new PhoneNumber(phoneNumberString);
                Date date = new Date(resultSet.getLong("date"));
                int permitNum = resultSet.getInt("permitNum");
                String address = resultSet.getString("address");

                users.add(new PotentialUser(name, representativeID, email, phoneNumber,
                         useType, password, date, permitNum, address));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return users;
    }
    public PotentialUser getUserFromEmail(String email){
        ResultSet resultSet = db.select("*", "NewApplicant", "email = " + email);
        try {
            while (resultSet.next()) {

                //Adding all stuff from database to new PotentialUser object
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                int usertype = resultSet.getInt("type");
                UserType useType = UserType.fromInt(usertype);
                int representativeID = resultSet.getInt("representativeID");
                String emailString = resultSet.getString("email");
                EmailAddress email1 = new EmailAddress(emailString);
                String phoneNumberString = resultSet.getString("phoneNumber");
                PhoneNumber phoneNumber = new PhoneNumber(phoneNumberString);
                Date date = new Date(resultSet.getLong("date"));
                int permitNum = resultSet.getInt("permitNum");
                String address = resultSet.getString("address");

                return(new PotentialUser(name, representativeID, email1, phoneNumber,
                        useType, password, date, permitNum, address));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }


}
