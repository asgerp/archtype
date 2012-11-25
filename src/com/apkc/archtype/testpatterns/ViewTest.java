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
        patterns = {@Pattern(name="testMvc", kind = "MVC", role="Model", references={"TestController"})}
        )
public class ViewTest {
    TestController testControlModel;

    public ViewTest() {
    }


}
