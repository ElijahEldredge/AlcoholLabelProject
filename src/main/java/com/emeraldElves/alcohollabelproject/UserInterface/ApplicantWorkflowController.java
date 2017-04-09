package com.emeraldElves.alcohollabelproject.UserInterface;

import com.emeraldElves.alcohollabelproject.Data.AlcoholDatabase;
import com.emeraldElves.alcohollabelproject.Data.Storage;
import com.emeraldElves.alcohollabelproject.Data.SubmittedApplication;
import com.emeraldElves.alcohollabelproject.Data.UserType;
import javafx.collections.FXCollections;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keionbis on 4/4/17.
 */

public class ApplicantWorkflowController {
    @FXML
    Button UpdateApplications;
    String Username;
    Main main;
    private List<SubmittedApplication> applications;

    @FXML
    ListView<String> list;

    public void init(String Username, Main main) {
        this.Username = Username;
        this.main = main;
        applications = Storage.getInstance().getApplicationsByApplicant(Username);
        List<String> applicationNames = new ArrayList<>();
        for (SubmittedApplication application : applications) {
            String name = "";
            name += application.getApplication().getAlcohol().getBrandName();
            switch (application.getStatus()) {
                case APPROVED:
                    name += " - Approved";
                    break;
                case REJECTED:
                    name += " - Rejected: " + application.getTtbMessage();
                    break;
                case PENDINGREVIEW:
                    name += " - Pending Review";
                    break;
            }
            applicationNames.add(name);
        }
        ObservableList<String> items = FXCollections.observableList(applicationNames);
        list.setItems(items);
    }

    public SubmittedApplication getSelectedApplication() {
        int i = list.getSelectionModel().getSelectedIndex();
        return applications.get(i);
    }

    public void reviseApplication(){
        main.loadNewApplicationPage(Username, getSelectedApplication());
    }

    public void ApplicationWorkflow() {
        main.loadUpdateApplicationPage(getSelectedApplication(), Username);
    }

    public void GoHome() {
        main.loadHomepage(UserType.APPLICANT, Username);
    }

    public void MakeNewApplication() {
        main.loadNewApplicationPage(Username);
    }
}
