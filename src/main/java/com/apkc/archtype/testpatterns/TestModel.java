/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.testpatterns;

import com.apkc.archtype.quals.Component;
import com.apkc.archtype.quals.Pattern;

/**
 *
 * @author asger
 */
@Component(
        name="TestModel",
        patterns = {@Pattern(name = "MVC", role="Model")}
        )
public class TestModel {
    TestController tc;

    public TestModel() {
    }

    
}
