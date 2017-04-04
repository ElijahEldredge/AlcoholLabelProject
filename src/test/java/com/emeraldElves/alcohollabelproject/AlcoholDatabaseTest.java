package com.emeraldElves.alcohollabelproject;

import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by elijaheldredge on 3/31/17.
 */
public class AlcoholDatabaseTest {
    AlcoholDatabase alcoholDatabase;
    Database db;

    @Before
    public void setup() throws InterruptedException {
        if (db != null)
            db.close();
        db = DatabaseController.getInstance().initDatabase("testDB");
        try {
            db.dropTable("SubmittedApplications");
            db.dropTable("ManufacturerInfo");
            db.dropTable("AlcoholInfo");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        db.close();
        db = DatabaseController.getInstance().initDatabase("testDB");
        alcoholDatabase = new AlcoholDatabase(db);
    }

    @Test
    public void testSubmitApplication() {
        AlcoholInfo alc = new AlcoholInfo(5, "name", "brand", ProductSource.DOMESTIC
                , AlcoholType.BEER, null); //alcohol info for test
        Date subDate = new Date(1997, 5, 20);
        ManufacturerInfo man = new ManufacturerInfo("dan", "WPI", "Dell-EMC", 69, 96, new PhoneNumber("5088888888"),
                new EmailAddress("dbmckay@wpi.edu"));//manufacturer info for test

        ApplicationInfo appInfo = new ApplicationInfo(subDate, man, alc);

        Applicant applicant = new Applicant(null);

        SubmittedApplication test = new SubmittedApplication(appInfo, ApplicationStatus.PENDINGREVIEW, applicant);
        assertTrue(alcoholDatabase.submitApplication(test));
        assertFalse(alcoholDatabase.submitApplication(test));


        AlcoholInfo alc2 = new AlcoholInfo(5, "name", "brand", ProductSource.DOMESTIC
                , AlcoholType.BEER, null); //alcohol info for test
        Date subDate2 = new Date(2000, 10, 20);
        ManufacturerInfo man2 = new ManufacturerInfo("dan", "WPI", "Dell-EMC", 69, 96, new PhoneNumber("5088888888"),
                new EmailAddress("dbmckay@wpi.edu"));//manufacturer info for test

        ApplicationInfo appInfo2 = new ApplicationInfo(subDate2, man2, alc2);

        Applicant applicant2 = new Applicant(null);

        SubmittedApplication test2 = new SubmittedApplication(appInfo2, ApplicationStatus.PENDINGREVIEW, applicant2);


        assertTrue(alcoholDatabase.submitApplication(test2));

        List<SubmittedApplication> applicationList = alcoholDatabase.getMostRecentUnapproved(10);
        assertEquals(2, applicationList.size());

        List<SubmittedApplication> applications = alcoholDatabase.getMostRecentUnapproved(1);
        assertEquals(1, applications.size());
    }

//    @Test
    public void testUnapproved() {
        //assertTrue(alcoholDatabase.getMostRecentUnapproved(10).isEmpty());
    }


}
