/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

/**
 *
 * @author higor
 */
public class Argument {
    private String name;
    private char type;
    private Float minValue;
    private Float maxValue;

    public Argument(String name, char type) {
        this.name = name;
        this.type = type;
    }

    public Argument(String name, char type, Float minValue, Float maxValue) {
        this.name = name;
        this.type = type;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public Float getMinValue() {
        return minValue;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }
    
    
    
    
    
         
}
