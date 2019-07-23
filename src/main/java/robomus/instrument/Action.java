/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import com.illposed.osc.OSCMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author higor
 */
public class Action {
    private String actionAddress;
    private List<Argument> arguments;
    private String csvInput;
    private OSCMessage oscMessage;

    public Action() {
    }
    
    public Action(String messageName, List params) {
        this.actionAddress = messageName;
        this.arguments = params;
    }

    public String getActionAddress() {
        return actionAddress;
    }

    public void setActionAddress(String actionAddress) {
        this.actionAddress = actionAddress;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public void setArguments(List<Argument> arguments) {
        this.arguments = arguments;
    }

    public List getArgumentsType(){
        List types = new ArrayList<>();

        for (Argument arg: arguments ) {
            types.add(arg.getType());
        }
        return types;
    }

    public OSCMessage getOscMessage() {
        return oscMessage;
    }

    public void setOscMessage(OSCMessage oscMessage) {
        this.oscMessage = oscMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(actionAddress, action.actionAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionAddress);
    }
}
