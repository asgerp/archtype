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
        patterns = {@Pattern(name="testLayered", kind = "Layered", role="LayerDATA")}
        )
public class Layer1 {
    Layer2 layer2;

    public Layer1() {
    }

    public Layer2 getLayer2() {
        return layer2;
    }

    public void setLayer2(Layer2 layer2) {
        this.layer2 = layer2;
    }
    
}