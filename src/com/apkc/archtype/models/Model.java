/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.models;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 * returns the model supplied as argument as a string
 * @author mundane
 */
public class Model {   
    public static String getModel(String str, Boolean verbose) {
        if(verbose){
            System.out.println("Opening model file: "+str+"\n");
        }
        FileInputStream fis;
        String fStr = "";
        try {
            fis = new FileInputStream(str);
            fStr = IOUtils.toString(fis);
            fis.close();
        } catch (Exception ioe) {     
            ioe.printStackTrace();
        }
        return fStr;
    }
}
