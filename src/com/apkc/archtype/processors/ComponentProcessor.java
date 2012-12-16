/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import com.apkc.archtype.alloy.AlloyTest;
import com.apkc.archtype.quals.Component;
import com.apkc.archtype.quals.Pattern;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Component processor. Process @Component annotations and writes alloy models to files
 * @author asger
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.apkc.archtype.quals.Component"})
public class ComponentProcessor extends AbstractProcessor {

    /**
     *
     * @param annotations annotations - contains the set of annotations that we are interested in processing. The annotations in the set should correspond with the list of annotations that we specify in the @SupportedAnnotationTypes annotation
     * @param roundEnv - this is the processing environment
     * @return boolean -  a boolean value is to indicate whether we have claimed ownership of the set of annotations passed by the processing environment in elements. "Claiming ownership" means that the set of annotations in elements are ours, and we have processed it. Return true if you claim ownership of them; false otherwise.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //processingEnv is a predefined member in AbstractProcessor class
        //Messager allows the processor to output messages to the environment
        Messager messager = processingEnv.getMessager();
        HashMap components = new HashMap();
        HashMap references = new HashMap();
        ArrayList<String> annotatedClasses = new ArrayList<>();
        boolean claimed = false;
        for (TypeElement te: annotations) {

            //Get the members that are annotated with Option
            for (Element e: roundEnv.getElementsAnnotatedWith(te)){
                claimed = true;
                annotatedClasses.add(e.getSimpleName().toString());
                processAnnotation(e, messager, components, references);
            }
        }

        //If there are any annotations, we will proceed to generate the annotation
        //processor in generateOptionProcessor method
        setUpReferences(annotatedClasses, references, components);
        ArrayList<String> files = generateAlloyModels(components);
        for(String file: files){
            // should return a data structure that encapsulates whether the check passed or not and a message
            AlloyTest.passToAlloy(file);
        }
        return claimed;
    }


    /**
     *
     * @param e - the element currently being worked on
     * @param m - the messager used for errors, notes and the like
     * @param components - a hashmap containing all components, stored under the pattern(s) they are part of
     */
    private void processAnnotation(Element e, Messager m, HashMap components, HashMap refs){
        Component com = e.getAnnotation(Component.class);

        // TODO do some analisys of enclosed contra p.references, do they
        List<? extends Element> enclosedElements = e.getEnclosedElements();
        ArrayList<String> stringRefs = new ArrayList<>();
        for(Element el: enclosedElements){
            String type = el.asType().toString();
            if(type.contains(".")){
                // is return value
                if(type.contains("()")){
                    stringRefs.add(type.substring(type.lastIndexOf(".") + 1));
                } else 
                    // is parameter
                    if (type.contains(")")){
                    stringRefs.add(type.substring(type.lastIndexOf(".") + 1, type.lastIndexOf(")") +1));
                }
                // is field 
                else{
                    stringRefs.add(type.substring(type.lastIndexOf(".") + 1));
                }
            }
        }
        refs.put(e.getSimpleName().toString(), stringRefs);
        ArrayList<ComponentRepresentation> cr;
        for(Pattern p : com.patterns()){
            ComponentRepresentation comRep = new ComponentRepresentation(e.getSimpleName().toString(), p.kind(), p.role());
            // check if the hashmap contains pattern p, if is does check if the list allready
            // contains the the component and extend the components references
            // else just add component
            if(components.containsKey(p.name())){
                cr =  (ArrayList<ComponentRepresentation>) components.get(p.name());
                boolean wasThere = false;/**
                 * for (ComponentRepresentation crIns : cr) {
                 * if(crIns.getComponentName().equals(comRep.getComponentName())){
                 * crIns.extendReferences(comRep.getRefreferences());
                 * wasThere = true;
                 * }
                 * }
                 * */
                if(!wasThere){
                    cr.add(comRep);
                }
            } else{
                cr = new ArrayList<>();
                components.put(p.name(), cr);
                cr.add(comRep);
            }
        }

    }
    /**
     *
     * @param components
     */
    private ArrayList<String> generateAlloyModels(HashMap components ) {
        ArrayList<String> fileNames = new ArrayList<>();
        if(components.isEmpty()){
            return fileNames;
        }

        Iterator componentIterator = components.entrySet().iterator();
        // Iterate over each pattern and write alloy model
        while(componentIterator.hasNext()){
            Map.Entry next = (Map.Entry) componentIterator.next();
            String patternName = (String) next.getKey();
            ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) next.getValue();

            
            try {
                

                String pat = "";
                StringBuilder contains = new StringBuilder("\telements = ");
                if(componentRepresentation != null){
                    Iterator<ComponentRepresentation> it = componentRepresentation.iterator();
                    while( it.hasNext()) {
                        ComponentRepresentation c = it.next();
                        contains.append(c.getComponentName());
                        if (it.hasNext()) {
                            pat = c.getPattern();
                            contains.append(" + ");
                        } else{
                            contains.append("\n}\n");
                        }
                    }
                }
                String generatedFilename = pat + "_" + patternName + "_configuration.als";
                BufferedWriter out = new BufferedWriter(new FileWriter(generatedFilename), 32768);
                out.write("open " + pat.toLowerCase() + "\n");
                out.write("one sig " + patternName + " extends Configuration { } {\n");
                out.write(contains.toString());
                if(componentRepresentation != null){
                    Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                    while( ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        out.write(c.toString());
                    }
                    ite = componentRepresentation.iterator();
                    while( ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        writeAsserts(out,pat,patternName,c.getRole(), c.getComponentName());

                    }
                    ite = componentRepresentation.iterator();
                    while( ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        writeCommands(out, pat, c.getComponentName());

                    }
                }
                // loop for asserts
                //                writeAsserts(out,pat,patternName);
                // loop for commands
                //              writeCommands(out, pat);

                out.close();
                fileNames.add(generatedFilename);
            } catch (IOException ex) {
                Logger.getLogger(ComponentProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fileNames;
    }

    /**
     * Should analyze which pattern is used and write the appropriate commands for that pattern, using the writer.
     * @param bw
     * @param pattern
     */
    private void writeCommands(BufferedWriter bw, String pattern, String componentName) throws IOException{
        bw.write("check " + componentName.toLowerCase() +" for 8\n");
    }
    /**
     *
     * @param bw
     * @param pattern
     * @param patternName
     * @throws IOException
     */
    private void writeAsserts(BufferedWriter bw, String pattern, String patternName, String role, String componentName) throws IOException{
        bw.write("assert "+ componentName.toLowerCase()+" {\n");
        bw.write("\t" + pattern.toLowerCase() + "_"+ role.toLowerCase()+"_style["+ patternName+"]\n");
        bw.write("}\n");
    }

    /**
     *
     * @param com
     * @param e
     */
    private void debugComponent(Component com, Element e){
        System.out.println("{");
        System.out.println("Component: ");
        System.out.println("\tname: " + e.getSimpleName());
        System.out.println( "\tfileName: " + e.getSimpleName());
        for(Pattern p: com.patterns()){
            System.out.println("\t\t{");
            System.out.println("\t\t\tPattern:");
            System.out.println("\t\t\t\tname: " + p.name());
            System.out.println("\t\t\t\tkind: " + p.kind());
            System.out.println("\t\t\t\trole: " + p.role());
            System.out.println("\t\t\t\trefs: {" );
            System.out.println("\t\t\t\t}");
            System.out.println("\t\t}");
        }
        System.out.println("}");
    }
    /**
     *
     * @param annotatedClasses
     * @param references
     * @param components
     */
    private void setUpReferences(ArrayList<String> annotatedClasses, HashMap references, HashMap components) {

        Iterator iterator = references.entrySet().iterator();
        // each annotedclass
        while(iterator.hasNext()){
            // annotated class
            Map.Entry next = (Map.Entry) iterator.next();
            String anClass = (String) next.getKey();
            Iterator compIt = components.entrySet().iterator();
            ArrayList<String> value = (ArrayList<String>) next.getValue();
            // each pattern
            while(compIt.hasNext()){
                Map.Entry pattern = (Map.Entry) compIt.next();

                ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) pattern.getValue();
                Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                boolean inPattern = false;
                ComponentRepresentation anClassCr = null;
                // is annotated class in pattern
                // each class in pattern
                while( ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    if (c.getComponentName().equals(anClass)){
                        inPattern = true;
                        anClassCr = c;
                    }
                }
                ite = componentRepresentation.iterator();
                // is
                while(ite.hasNext()){
                    ComponentRepresentation c = ite.next();
                    if(value.contains(c.getComponentName()) && inPattern){
                        anClassCr.extendReferences(c.getComponentName());
                    }
                }
            }
        }
    }
}
