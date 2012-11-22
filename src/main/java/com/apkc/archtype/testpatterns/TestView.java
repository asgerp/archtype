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
        name="TestView",
        patterns = {@Pattern(name="testMvc",kind = "MVC", role="View", references={"TestController"})}
        )
public class TestView {
    TestController testControlView;

    public TestView() {
    }

}
