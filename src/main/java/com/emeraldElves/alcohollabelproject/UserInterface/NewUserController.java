package com.emeraldElves.alcohollabelproject.UserInterface;

import com.emeraldElves.alcohollabelproject.Data.*;
import com.emeraldElves.alcohollabelproject.Log;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;


import java.util.Date;

/**
 * Created by Essam on 4/4/2017.
 */
public class NewUserController implements IController {
    @FXML
    PasswordField passwordField;
    @FXML
    TextField representativeID;
    @FXML
    TextField Name;
    @FXML
    TextField emailAddress;
    @FXML
    TextField phoneNumber;
    @FXML
    VBox errorMsg;
    @FXML
    TextField permitNumText;
    @FXML
    TextField addressText;
    @FXML
    RadioButton applicantBtn;
    @FXML
    RadioButton agentBtn;
    @FXML
    Label accountError;
    @FXML
    Label emailError;
    @FXML
    Label phoneNumError;
    @FXML
    Label passwordError;
    @FXML
    Label permitNumError;
    @FXML
    Label nameError;
    @FXML
    Label addressError;
    @FXML
    Label repIDError;
    private int repID;
    private Main main;
    private int userTypeInt = -1;
    private String FullName;
    private String password;
    private String address;
    private PasswordStrengthChecker CheckStrength;
    public NewUserController() {

    }

    public void init(Bundle bundle){
        this.init(bundle.getMain("main"));
    }

    public void init(Main main) {
        this.main = main;
        ToggleGroup accountType = new ToggleGroup();
        applicantBtn.setToggleGroup(accountType);
        agentBtn.setToggleGroup(accountType);
        CheckStrength = new PasswordStrengthChecker();
    }

    public void setUserTypeAgent(){
        userTypeInt = 0;
        permitNumText.setDisable(true);
    }

    public void setUserTypeApplicant(){
        userTypeInt = 1;
        permitNumText.setDisable(false);
    }

    public void createPotentialUser(){
        accountError.setText("");
        emailError.setText("");
        phoneNumError.setText("");
        passwordError.setText("");
        permitNumError.setText("");
        repIDError.setText("");
        addressError.setText("");
        nameError.setText("");
        if(!(applicantBtn.isSelected() || agentBtn.isSelected())) {
            accountError.setText("You need to select an account type");
            return;
        }
        if(!CheckStrength.isPasswordValid(passwordField.getText()))
        {
            passwordError.setText("Enter a valid Password");
            return;
        }




        EmailAddress Email  = new EmailAddress(emailAddress.getText().toString());
        PhoneNumber PhoneNumber = new PhoneNumber(phoneNumber.getText().toString());
        if(!PhoneNumber.isValid()){
            phoneNumError.setText("Enter a valid phone number");
            return;
        }

        if(!Email.isValid()){
            emailError.setText("Enter a valid email address");
            return;
        }

        int permitNum;

        if(permitNumText.isDisabled()){
            permitNum = -1;
        }


        if(permitNumText.isEditable()&&permitNumText.getText().trim().isEmpty()){
            permitNumError.setText("Enter a valid permit number");
            return;
        }



        if(representativeID.getText().trim().isEmpty()){
            repIDError.setText("Enter a valid representative ID");
            return;
        }






        if (addressText.getText().trim().isEmpty())
        {
            addressError.setText("Enter a valid address");
            return;
        }




        if (Name.getText().trim().isEmpty())
        {
            nameError.setText("Enter a valid name");
            return;
        }


        //Setting all the fields for the new potential user


        UserType userType = UserType.fromInt(userTypeInt);
        java.util.Date newDate = new Date();
         Email  = new EmailAddress(emailAddress.getText().toString());
         PhoneNumber = new PhoneNumber(phoneNumber.getText().toString());
        password = passwordField.getText();
        permitNum = Integer.parseInt(representativeID.getText());//check if field is not null
        address = addressText.getText();//representative ID
        repID = Integer.parseInt(permitNumText.getText());//check if field is not null
        FullName = Name.getText();



        if (Storage.getInstance().applyForUser(new PotentialUser(FullName,repID ,Email, PhoneNumber, userType,
                password, newDate, permitNum, address))){
            errorMsg.setVisible(false);
            main.loadHomepage();
        } else {
            errorMsg.setVisible(true);
        }
    }


    //shouldn't be needed anymore
    /*
    public void createApplicant(){
        //Setting all the fields for the new potential user
        String password = passwordField.getText();
        String FullName = Name.getText();
        UserType userType = UserType.APPLICANT;
        int repID = Integer.parseInt(representativeID.getText());//representative ID
        java.util.Date newDate =DateHelper.getDate(date.getValue().getDayOfMonth(),date.getValue().getMonthValue() - 1,date.getValue().getYear());
        EmailAddress Email  = new EmailAddress(emailAddress.getText().toString());
        PhoneNumber PhoneNumber = new PhoneNumber(phoneNumber.getText().toString());
        int permitNum = Integer.parseInt(permitNumText.getText());
        String address = addressText.getText();

        if (Storage.getInstance().applyForUser(new PotentialUser(FullName,repID ,Email, PhoneNumber, userType,
                password, newDate, permitNum, address))){
            errorMsg.setVisible(false);
            main.loadHomepage();
        } else {
            errorMsg.setVisible(true);
        }
    }
    */

    public void checkPassword(){
        if(CheckStrength.isPasswordValid(passwordField.getText())){
            Log.console("Good");
        }

    }

    public void GoHome(){
        main.loadHomepage();
    }
}
