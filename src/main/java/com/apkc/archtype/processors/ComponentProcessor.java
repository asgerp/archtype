/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import com.apkc.archtype.quals.Component;
import com.apkc.archtype.quals.Pattern;
import java.util.Set;
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
        boolean claimed = false;
        for (TypeElement te: annotations) {

            //Get the members that are annotated with Option
            for (Element e: roundEnv.getElementsAnnotatedWith(te)){
                claimed = true;
                processAnnotation(e, messager);
            }
        }

        //If there are any annotations, we will proceed to generate the annotation
        //processor in generateOptionProcessor method

        return claimed;
    }
    private void processAnnotation(Element e, Messager m){
        
        Component com = e.getAnnotation(Component.class);
        System.out.println("{");
        System.out.println("Component: ");
        System.out.println("\tname: " + com.name());
        System.out.println( "\tfileName: " + e.getSimpleName());
        for(Pattern p : com.patterns()){
            System.out.println("\t\t{");
            System.out.println("\t\t\tPattern:");
            System.out.println("\t\t\t\tname: " + p.name());
            System.out.println("\t\t\t\trole: " + p.role());
            System.out.println("\t\t}");
        }
        System.out.println("}");
    }
}
