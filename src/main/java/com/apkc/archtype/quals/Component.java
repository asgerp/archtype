/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.quals;

import java.lang.annotation.*;

/**
 *
 * @author asger
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Component {
    public String name();
    String[] composes() default{};
    String[] patterns() default{};
}
