/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.models;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.apache.commons.io.IOUtils;

/**
 * returns the model supplied as argument as a string
 * @author mundane
 */
public class Model {   
    
    public String getModel(String str, Boolean verbose) {
        if(verbose){
            System.out.println("Opening model file: "+str+"\n");
        }
        StringBuilder fStr = new StringBuilder();
        try {
            InputStream stream = getClass().getResourceAsStream(str+".als");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            String line = "";
            while(line != null) {
                fStr.append(line+"\n");
                line = br.readLine();
            }
            br.close();
        } catch (Exception ioe) {     
            ioe.printStackTrace();
        }
        return fStr.toString().trim();
    }
}
