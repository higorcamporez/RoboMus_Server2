/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robomus.instrument;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author higor
 */
public class Delay {
    
    private Long messageId;
    private String input1;
    private String input2;
    private Long delay = (long)-1;

    public Delay(Long messageId) {
        this.messageId = messageId;

    }

    public Delay(Long messageId, String input1, String input2) {
        this(messageId);
        this.input1 = input1;
        this.input2 = input2;
    }
    
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public String getInput1() {
        return input1;
    }

    public void setInput1(String input1) {
        this.input1 = input1;
    }

    public String getInput2() {
        return input2;
    }

    public void setInput2(String input2) {
        this.input2 = input2;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Delay other = (Delay) obj;
        if (!Objects.equals(this.messageId, other.messageId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Delay{" + "messageId=" + messageId + ", input1=" + input1 + ", input2=" + input2 + ", delay=" + delay + '}';
    }
    
    
}
