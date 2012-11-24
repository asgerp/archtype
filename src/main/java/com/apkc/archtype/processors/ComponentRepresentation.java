/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.processors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;




/**
 *
 * @author asger
 */
public class ComponentRepresentation {
    private String componentName;
    private String pattern;
    private String role;
    private ArrayList<String> references;

    /**
     *
     * @param componentName
     * @param pattern
     * @param role
     * @param references
     */
    public ComponentRepresentation(String componentName, String pattern, String role, String[] references) {
        this.componentName = componentName;
        this.pattern = pattern;
        this.role = role;
        this.references = new ArrayList(Arrays.asList(references));
    }

    public String getComponentName() {
        return componentName;
    }

    public String getPattern() {
        return pattern;
    }

    public String getRole() {
        return role;
    }

    public ArrayList<String> getRefreferences() {
        return references;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setRefererences(ArrayList<String> refererences) {
        this.references = refererences;
    }

    public void extendReferences(ArrayList<String> extraRefs){
        for (String ref : extraRefs) {
            if(!references.contains(ref)){
                references.add(ref);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("one sig ").append(componentName).append(" extends ").append(role).append(" { } {\n");
        sb.append("\treferences = ");
        Iterator<String> it = references.iterator();
                while( it.hasNext()) {
                    sb.append(it.next());
                    if (it.hasNext()) {
                        sb.append(" + ");
                    } else{
                        sb.append("\n");
                    }
                }
        sb.append("}\n");
        return sb.toString();
        //return "ComponentRepresentation{" + "componentName=" + componentName + ", pattern=" + pattern + ", role=" + role + ", refreferences=" + references + '}';
    }





}
