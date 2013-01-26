/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import com.apkc.archtype.alloy.AlloyTest;
import com.apkc.archtype.models.Model;
import com.apkc.archtype.quals.ArchTypeComponent;
import com.apkc.archtype.quals.Pattern;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import org.apache.commons.lang3.*;
import org.apache.log4j.Logger;

/**
 * ArchTypeComponent processor. Process
 *
 * @ArchTypeComponent annotations and writes alloy models to files
 * @author asger
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({"com.apkc.archtype.quals.ArchTypeComponent"})
public class ComponentProcessor extends AbstractProcessor {
    final static Logger log = Logger.getLogger(ComponentProcessor.class.getName());
    /**
     *
     * @param annotations annotations - contains the set of annotations that we
     * are interested in processing. The annotations in the set should
     * correspond with the list of annotations that we specify in the
     * @SupportedAnnotationTypes annotation
     * @param roundEnv - this is the processing environment
     * @return boolean - a boolean value is to indicate whether we have claimed
     * ownership of the set of annotations passed by the processing environment
     * in elements. "Claiming ownership" means that the set of annotations in
     * elements are ours, and we have processed it. Return true if you claim
     * ownership of them; false otherwise.
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        //processingEnv is a predefined member in AbstractProcessor class
        //Messager allows the processor to output messages to the environment
        Messager messager = processingEnv.getMessager();
        HashMap<String,ArrayList<ComponentRepresentation>> components = new HashMap();
        HashMap references = new HashMap();
        ArrayList<String> annotatedClasses = new ArrayList<>();
        boolean claimed = false;
        for (TypeElement te : annotations) {

            //Get the members that are annotated with Option
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                claimed = true;
                annotatedClasses.add(e.getSimpleName().toString());
                processAnnotation(e, messager, components, references);
            }
        }

        //If there are any annotations, we will proceed to generate the annotation
        //processor in generateOptionProcessor method
        setUpReferences(references, components);
        File f = new File("components.ser");
        int size = components.size();
        log.debug("no of entries before write: " + size);
        try {
            f.createNewFile();
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            Iterator iterator = components.entrySet().iterator();
            oos.writeObject(size);
            while(iterator.hasNext()){
                Map.Entry next = (Map.Entry) iterator.next();
                String patternName = (String) next.getKey();
                log.debug("writes pattern: " + patternName);
                oos.writeObject(patternName);
                oos.writeObject(next.getValue());
            }


            oos.close();
        } catch (IOException ex) {
            log.warn(ex);
        }
        HashMap<String,ArrayList<ComponentRepresentation>> readComponents = new HashMap();
        try {
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)));
            int reads = 0;
            int objects = (int) ois.readObject();
            log.debug("no of entries after read = " + objects);

            while(reads < objects){
                String ptn = (String)ois.readObject();
                ArrayList<ComponentRepresentation> acr = (ArrayList<ComponentRepresentation>)ois.readObject();
                readComponents.put(ptn,acr);
                reads++;
            }

            if(readComponents == null){
                log.debug("BLERGH");
            } else{
                Iterator iterator = readComponents.entrySet().iterator();
                if(iterator == null) {
                    log.debug("NULL");
                }
                while(iterator.hasNext()){
                    Map.Entry next = (Map.Entry) iterator.next();
                    String patternName = (String) next.getKey();
                    log.debug("Has read = " + patternName);
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            log.warn(ex);
        }
        
        ArrayList<String> models = generateAlloyModelsStr(components);
        for (String model : models) {
            // should return a data structure that encapsulates whether the check passed or not and a message
            //AlloyTest.passToAlloy(model);
            AlloyTest.passStrToAlloy(model);
        }
        return claimed;
    }

    /**
     *
     * @param e - the element currently being worked on
     * @param m - the messager used for errors, notes and the like
     * @param components - a hashmap containing all components, stored under the
     * pattern(s) they are part of
     */
    private void processAnnotation(Element e, Messager m, HashMap components, HashMap refs) {
        ArchTypeComponent com = e.getAnnotation(ArchTypeComponent.class);
        // TODO do some analisys of enclosed contra p.references, do they
        List<? extends Element> enclosedElements = e.getEnclosedElements();
        ArrayList<String> stringRefs = new ArrayList<>();
        for (Element el : enclosedElements) {
            String type = el.asType().toString();
            if (type.contains(".")) {
                // is return value
                if (type.contains("()")) {
                    stringRefs.add(type.substring(type.lastIndexOf(".") + 1));
                } else // is parameter check for more than one param
                if (type.contains(")")) {
                    String[] params = StringUtils.split(type.substring(type.lastIndexOf(".") + 1, type.lastIndexOf(")") + 1), ',');
                    stringRefs.addAll(Arrays.asList(params));
                } // is field
                else {
                    stringRefs.add(type.substring(type.lastIndexOf(".") + 1));
                }
            }
        }
        refs.put(e.getSimpleName().toString(), stringRefs);
        ArrayList<ComponentRepresentation> cr;
        for (Pattern p : com.patterns()) {
            ComponentRepresentation comRep = new ComponentRepresentation(e.getSimpleName().toString(), p.kind(), p.role());
            // check if the hashmap contains pattern p, if is does check if the list allready
            // contains the the component and extend the components references
            // else just add component
            if (components.containsKey(p.name())) {
                cr = (ArrayList<ComponentRepresentation>) components.get(p.name());
                boolean wasThere = false;
                /**
                 * for (ComponentRepresentation crIns : cr) {
                 * if(crIns.getComponentName().equals(comRep.getComponentName())){
                 * crIns.extendReferences(comRep.getRefreferences()); wasThere =
                 * true; } }
                 *
                 */
                if (!wasThere) {
                    cr.add(comRep);
                }
            } else {
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
    private ArrayList<String> generateAlloyModels(HashMap components) {
        ArrayList<String> fileNames = new ArrayList<>();
        if (components.isEmpty()) {
            return fileNames;
        }

        Iterator componentIterator = components.entrySet().iterator();
        // Iterate over each pattern and write alloy model
        while (componentIterator.hasNext()) {
            Map.Entry next = (Map.Entry) componentIterator.next();
            String patternName = (String) next.getKey();
            ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) next.getValue();

            try {
                String pat = "";
                StringBuilder contains = new StringBuilder("\telements = ");
                if (componentRepresentation != null) {
                    Iterator<ComponentRepresentation> it = componentRepresentation.iterator();
                    while (it.hasNext()) {
                        ComponentRepresentation c = it.next();
                        contains.append(c.getComponentName());
                        if (it.hasNext()) {
                            pat = c.getPattern();
                            contains.append(" + ");
                        } else {
                            contains.append("\n}\n");
                        }
                    }
                }
                String generatedFilename = pat + "_" + patternName + "_configuration.als";
                BufferedWriter out = new BufferedWriter(new FileWriter(generatedFilename), 32768);
                out.write("open " + pat.toLowerCase() + "\n");
                out.write("one sig " + patternName + " extends Configuration { } {\n");
                out.write(contains.toString());
                if (componentRepresentation != null) {
                    Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                    while (ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        out.write(c.toString());
                    }
                    ite = componentRepresentation.iterator();
                    while (ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        writeAsserts(out, pat, patternName, c.getRole(), c.getComponentName());

                    }
                    ite = componentRepresentation.iterator();
                    while (ite.hasNext()) {
                        ComponentRepresentation c = ite.next();
                        writeCommands(out, pat, c.getComponentName());

                    }
                }

                out.close();
                fileNames.add(generatedFilename);
            } catch (IOException ex) {
                log.warn(ex);
            }
        }
        return fileNames;
    }

    /**
     *
     * @param components
     */
    private ArrayList<String> generateAlloyModelsStr(HashMap components) {
        ArrayList<String> models = new ArrayList<>();
        if (components.isEmpty()) {
            return models;
        }

        Iterator componentIterator = components.entrySet().iterator();
        // Iterate over each pattern and write alloy model
        while (componentIterator.hasNext()) {
            Map.Entry next = (Map.Entry) componentIterator.next();
            String patternName = (String) next.getKey();
            ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) next.getValue();

            StringBuilder finalStr = new StringBuilder("");
            String pat = "";
            StringBuilder contains = new StringBuilder("\telements = ");
            if (componentRepresentation != null) {
                Iterator<ComponentRepresentation> it = componentRepresentation.iterator();
                while (it.hasNext()) {
                    ComponentRepresentation c = it.next();
                    contains.append(c.getComponentName());
                    if (it.hasNext()) {
                        pat = c.getPattern();
                        contains.append(" + ");
                    } else {
                        contains.append("\n}\n");
                    }
                }
            }
            Model model = new Model();
            finalStr.append(model.getModel(pat.toLowerCase(), Boolean.TRUE));
            //finalStr.append("open " + pat.toLowerCase() + "\n");
            finalStr.append("\none sig " + patternName + " extends Configuration { } {\n");
            finalStr.append(contains.toString());

            if (componentRepresentation != null) {
                Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    finalStr.append(c.toString());
                }
                ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    writeAssertsStr(finalStr, pat, patternName, c.getRole(), c.getComponentName());

                }
                ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    writeCommandsStr(finalStr, pat, c.getComponentName());
                }
            }
            System.out.println(finalStr);
            models.add(finalStr.toString());
        }
        return models;
    }

    /**
     * Should analyze which pattern is used and write the appropriate commands
     * for that pattern, using the writer.
     *
     * @param bw
     * @param pattern
     */
    private void writeCommands(BufferedWriter bw, String pattern, String componentName) throws IOException {
        bw.write("check " + componentName.toLowerCase() + " for 8\n");
    }

    private void writeCommandsStr(StringBuilder sb, String pattern, String componentName) {
        sb.append("check " + componentName.toLowerCase() + " for 1 Configuration, 8 Element\n");
    }

    /**
     *
     * @param bw
     * @param pattern
     * @param patternName
     * @throws IOException
     */
    private void writeAsserts(BufferedWriter bw, String pattern, String patternName, String role, String componentName) throws IOException {
        bw.write("assert " + componentName.toLowerCase() + " {\n");
        bw.write("\t" + pattern.toLowerCase() + "_" + role.toLowerCase() + "_style[" + patternName + "]\n");
        bw.write("}\n");
    }

    private void writeAssertsStr(StringBuilder sb, String pattern, String patternName, String role, String componentName) {
        sb.append("assert " + componentName.toLowerCase() + " {\n");
        sb.append("\t" + pattern.toLowerCase() + "_" + role.toLowerCase() + "_style[" + patternName + "]\n");
        sb.append("}\n");
    }

    /**
     *
     * @param com
     * @param e
     */

    private void debugComponent(ArchTypeComponent com, Element e){
        log.debug("{");
        log.debug("Component: ");
        log.debug("\tname: " + e.getSimpleName());
        log.debug( "\tfileName: " + e.getSimpleName());
        for(Pattern p: com.patterns()){
            log.debug("\t\t{");
            log.debug("\t\t\tPattern:");
            log.debug("\t\t\t\tname: " + p.name());
            log.debug("\t\t\t\tkind: " + p.kind());
            log.debug("\t\t\t\trole: " + p.role());
            log.debug("\t\t\t\trefs: {" );
            log.debug("\t\t\t\t}");
            log.debug("\t\t}");
        }
        log.debug("}");
    }

    /**
     *
     * @param references
     * @param components
     */
    private void setUpReferences(HashMap references, HashMap components) {
        Iterator iterator = references.entrySet().iterator();
        // each annotedclass
        while (iterator.hasNext()) {
            // annotated class
            Map.Entry next = (Map.Entry) iterator.next();
            String anClass = (String) next.getKey();
            Iterator compIt = components.entrySet().iterator();
            ArrayList<String> value = (ArrayList<String>) next.getValue();
            // each pattern
            while (compIt.hasNext()) {
                Map.Entry pattern = (Map.Entry) compIt.next();

                ArrayList<ComponentRepresentation> componentRepresentation = (ArrayList<ComponentRepresentation>) pattern.getValue();
                Iterator<ComponentRepresentation> ite = componentRepresentation.iterator();
                boolean inPattern = false;
                ComponentRepresentation anClassCr = null;
                // is annotated class in pattern
                // each class in pattern
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    if (c.getComponentName().equals(anClass)) {
                        inPattern = true;
                        anClassCr = c;
                    }
                }
                ite = componentRepresentation.iterator();
                while (ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    if (value.contains(c.getComponentName()) && inPattern) {
                        anClassCr.extendReferences(c.getComponentName());
                    }
                }
            }
        }
    }
}
