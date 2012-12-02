/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apkc.archtype.testpatterns;

import com.apkc.archtype.quals.*;
/**
 *
 * @author asger
 */
@Component(
        name="TestController3",
        patterns = {@Pattern(name="testMvc",kind = "MVC", role="Controller", references={"TestView", "TestController"})}
        )
public class TestController3 {
    TestController testControlControl;
    TestController testViewControl;
    TestModel testModelControl;


    public TestController3() {
    }

}
