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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class demonstrates how to access Alloy4 via the API.
 */
public final class AlloyTest {
    
    // patterns we'll be supporting, extend as necessary
    private enum Pattern {
        MVC, NOTMVC
    }
    
    /**
     * writes a string to a file, overwrites preexisting files
     * @param content - file content
     * @param filename - name of file to write to
     */
    private void writeToFile(String content,String filename) {
        try {
            // delete preexisting file
            File file = new File(filename);
            if (file.exists()) {
                file.delete();
            }
            // output using FileWriter
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void hej(){
        System.out.println("hej");
    }
    /**
     * @param filename - the file to pass to alloy for interpreting 
     */
    public static void passToAlloy(String filename) {
        System.out.println("here");
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
        System.out.println("=========== Parsing+Typechecking " + filename + " ===========");
        Module world = null;
        try {
            world = CompUtil.parseEverything_fromFile(rep, null, filename);
        } catch (Err ex) {
            Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Choose some default options for how you want to execute the commands
        A4Options options = new A4Options();
        options.solver = A4Options.SatSolver.SAT4J;
        for (Command command : world.getAllCommands()) {
            // Execute the command
            System.out.println("=========== Command " + command + ": ===========");
            A4Solution ans = null;
            try {
                ans = TranslateAlloyToKodkod.execute_command(rep, world.getAllReachableSigs(), command, options);
            } catch (Err ex) {
                Logger.getLogger(AlloyTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            // Print the outcome
            System.out.println(ans);
            System.out.println("Satisfiable: " + ans.satisfiable());
        }
    }
    
    public static void main(String[] args) throws Err {
        // quick test
        String filename = "alloy_models/testMvc_configuration.als";
        passToAlloy(filename);
    }
}
