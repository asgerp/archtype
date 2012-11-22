/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

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
        messager.printMessage(Diagnostic.Kind.NOTE, "Starting processor");
        HashMap components = new HashMap();
        boolean claimed = false;
        for (TypeElement te: annotations) {

            //Get the members that are annotated with Option
            for (Element e: roundEnv.getElementsAnnotatedWith(te)){
                claimed = true;
                processAnnotation(e, messager, components);
            }
        }

        //If there are any annotations, we will proceed to generate the annotation
        //processor in generateOptionProcessor method
        generateAlloyModels(components);
        return claimed;
    }
    /**
     *
     * @param e - the element currently being worked on
     * @param m - the messager used for errors, notes and the like
     * @param c - a hashmap containing all components, stored under the pattern(s) they are part of
     */
    private void processAnnotation(Element e, Messager m, HashMap c){
        Component com = e.getAnnotation(Component.class);

        // TODO do some analisys of enclosed contra p.references, do they
        List<? extends Element> enclosedElements = e.getEnclosedElements();

        ArrayList<ComponentRepresentation> cr;
        System.out.println("{");
        System.out.println("Component: ");
        System.out.println("\tname: " + com.name());
        System.out.println( "\tfileName: " + e.getSimpleName());
        for(Pattern p : com.patterns()){
            ComponentRepresentation comRep = new ComponentRepresentation(com.name(), p.kind(), p.role(), p.references());
            if(c.containsKey(p.name())){
                cr =  (ArrayList<ComponentRepresentation>) c.get(p.name());
            } else{
                cr = new ArrayList<ComponentRepresentation>();
                c.put(p.name(), cr);
            }
            cr.add(comRep);
            String[] refs = p.references();
            System.out.println("\t\t{");
            System.out.println("\t\t\tPattern:");
            System.out.println("\t\t\t\tname: " + p.name());
            System.out.println("\t\t\t\tkind: " + p.kind());
            System.out.println("\t\t\t\trole: " + p.role());
            System.out.println("\t\t\t\trefs: {" );
            for(String ref: p.references()){
                System.out.println("\t\t\t\t\tref: " + ref + " ");
            }
            /*for(Element ee: enclosedElements){
             * System.out.println(ee.+ " " + ee.asType().toString());
             * if(ee.getKind().isClass()){
             *
             * String actualRef = ee.asType().toString();
             * System.out.println(actualRef);
             * actualRef = actualRef.substring(actualRef.lastIndexOf("."));
             * if(Arrays.asList(refs).contains(actualRef)){
             * System.out.println("\t\t\t\t\t"+actualRef);
             * }else{
             * m.printMessage(Diagnostic.Kind.ERROR, "illegal reference", e);
             * }
             * }
             * }*/
            System.out.println("\t\t\t\t}");
            System.out.println("\t\t}");
        }
        System.out.println("}");
    }

    private void generateAlloyModels(HashMap coms ) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("alloy_models"), 32768);
        } catch (IOException ex) {
            Logger.getLogger(ComponentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }



        Set keySet = coms.entrySet();
        Iterator i = keySet.iterator();
        String patternName = "";
        ArrayList<ComponentRepresentation> comRe = null;
        while(i.hasNext()){
            Map.Entry next = (Map.Entry) i.next();
            patternName = (String) next.getKey();
            comRe = (ArrayList<ComponentRepresentation>) next.getValue();
        }
        String generatedFilename = patternName + "_configuration.als";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(generatedFilename), 32768);

            String pat = "";
            StringBuilder contains = new StringBuilder("\telements = ");
            if(comRe != null){
                Iterator<ComponentRepresentation> it = comRe.iterator();
                while( it.hasNext()) {
                    ComponentRepresentation c = it.next();
                    contains.append(c.getComponentName());
                    if (it.hasNext()) {
                        pat = c.getPattern();
                        contains.append(" + ");
                    } else{
                        contains.append("\n");
                    }
                }
            }
            out.write("open " + pat + "\n");
            out.write("one sig " + patternName + " extends Configuration { } {\n");
            out.write(contains.toString());
            if(comRe != null){
                Iterator<ComponentRepresentation> ite = comRe.iterator();
                while( ite.hasNext()) {
                    ComponentRepresentation c = ite.next();
                    out.write(c.toString());
                }
            }
            out.write("assert conforms {\n");
            out.write("\t" + pat +"_style["+ patternName+"]\n");
            out.write("}\n");
            out.write("check conforms\n");
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(ComponentProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    class AlloyModel{

    }
}
