package com.emeraldElves.alcohollabelproject;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by keionbis on 4/11/17.
 */
public class ProxyLabelImage implements ILabelImage {
    @FXML
    ImageView imageView;
    LabelImage realImage;
    String fileName;
    public  ProxyLabelImage(String fileName){
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }


    public void display() {
        if (realImage == null) { // Lazy Loading
            realImage = new LabelImage(fileName);
        }
        realImage.display();
    }
    }

//    Path Imagedir = Paths.get("Labels");
//    Path imagepath = Imagedir.resolve(fileName);
//    Image image = new Image(String.valueOf(imagepath));
//        return(realImage);