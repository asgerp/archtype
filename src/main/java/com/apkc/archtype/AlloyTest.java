/* Alloy Analyzer 4 -- Copyright (c) 2006-2009, Felix Chang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.apkc.archtype;

import edu.mit.csail.sdg.alloy4.A4Reporter;
import edu.mit.csail.sdg.alloy4.Err;
import edu.mit.csail.sdg.alloy4.ErrorWarning;
import edu.mit.csail.sdg.alloy4compiler.ast.Command;
import edu.mit.csail.sdg.alloy4compiler.ast.Module;
import edu.mit.csail.sdg.alloy4compiler.parser.CompUtil;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Options;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;
import edu.mit.csail.sdg.alloy4compiler.translator.TranslateAlloyToKodkod;
import java.io.*;

/**
 * This class demonstrates how to access Alloy4 via the API.
 */
public final class AlloyTest {
    
    // patterns we'll be supporting, extend as necessary
    private enum Pattern {
        MVC, NOTMVC
    }
    /**
     * doTest generates a piece of alloy code to test a generated model's 
     * conformity with a general pattern such as MVC, client-server etc.
     * @param modelName - name used in the alloy code for the root sig describing the model
     * @param modelStr - alloy code describing the model to test
     * @param genPatternName - name of pattern to test conformity with
     * Ex. - doTest("EHR",<some alloy code here in str>,"MVC")
     */
    public void doTest(String modelName, String modelStr, String genPatternName) {
        Pattern p;
        // test if supported pattern
        try {
            p = Pattern.valueOf(genPatternName);
        } catch (IllegalArgumentException iae) {
            System.out.println(genPatternName+ "is an unsupported pattern!");
            //iae.printStackTrace();
        }
        // conformity check command we need to customize and append to file
        String conformStr = "assert conforms { %PATNAME%_style[%MODELNAME%] } check conforms";
        String content = "";
        //gen file content
        content = "open " + genPatternName + "\n" + modelStr;
        conformStr = conformStr.replace("%PATNAME%",genPatternName);
        conformStr = conformStr.replace("%MODELNAME%",modelName);
        content += conformStr;
        
        // output content to file
        String fname = "alloy_models/alloyTest.als";
        writeToFile(content,fname);
        
        // pass file to alloy for interpreting
        try {
            passToAlloy(fname);
        } catch (Err e) {
            System.out.println("Error: "+e.msg);
        } 
    }
    /**
     * writes a string to a file, overwrites preexisting files
     * @param content - file content
     * @param filename - name of file to write to
     */
    private void writeToFile(String content,String filename) {
        FileWriter fstream = null;
        try {
            // delete preexisting file
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
            // output using FileWriter
            fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * @param filename - the file to pass to alloy for interpreting 
     */
    private static void passToAlloy(String filename) throws Err {
        // boilerplate alloy4 reporter code
        A4Reporter rep = new A4Reporter() {
            // here we choose to display each "warning" by printing it to System.out
            @Override
            public void warning(ErrorWarning msg) {
                System.out.print("Relevance Warning:\n" + (msg.toString().trim()) + "\n\n");
                System.out.flush();
            }
        };
        // Parse+typecheck the model
        System.out.println("=========== Parsing+Typechecking " + filename + " =============");
        Module world = CompUtil.parseEverything_fromFile(rep, null, filename);

        // Choose some default options for how you want to execute the commands
        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;
        for (Command command : world.getAllCommands()) {
            // Execute the command
            System.out.println("============ Command " + command + ": ============");
            A4Solution ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);
            // Print the outcome
            System.out.println(ans);
            System.out.println("Satisfiable: " + ans.satisfiable());
        }
    }
    
    public static void main(String[] args) throws Err {
        // quick test
        String filename = "alloy_models/test.als";
        passToAlloy(filename);
    }
}
