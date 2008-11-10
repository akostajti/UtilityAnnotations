/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package annoj.sample;

import annoj.annotation.Include;
import annoj.annotation.Loggable;

/**
 *
 * @author Tajti Ákos
 */
public class TestClass {

    private int x;
    public String name;
    public double d;

    public TestClass(int x, String name, double d) {
        this.x = x;
        this.name = name;
        this.d = d;
    }
    
    public void x(String s){
        System.out.println("Ez meghívódott");
    }
}
