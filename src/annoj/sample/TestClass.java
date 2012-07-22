package annoj.sample;

import annoj.annotation.Loggable;

/**
 *
 * @author Akos Tajti
 */
@Loggable
public class TestClass {

    private int x;
    public String name;
    public double d;

    public TestClass(){
        
    }
    
    public TestClass(int x, String name, double d) {
        this.x = x;
        this.name = name;
        this.d = d;
    }
    
    public void x(String s){
        System.out.println("invoked");
    }
}
