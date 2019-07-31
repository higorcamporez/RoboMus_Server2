package robomus.server;

import java.util.Collections;
import java.util.List;

public class Buffer extends Thread{
    private volatile List<RoboMusMessage> messages;

    public void Buffer(){

    }

    public List<RoboMusMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<RoboMusMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(RoboMusMessage roboMusMessage){
        //System.out.println("add");
        this.messages.add(roboMusMessage);
        // ordenar
        Collections.sort(this.messages);
    }

    @Override
    public void run() {

        while(true){

            if(!this.messages.isEmpty()){
                if((this.messages.get(0).getCompensatedTimestamp().getTime()) <
                    System.currentTimeMillis()) {

                    //enviar msg ao instrumento
                    this.messages.get(0).send();

                    //retirar do buffer
                    this.messages.remove(0);
                    System.out.println("buffer: enviou msg: "+System.currentTimeMillis());
                }

            }
        }
    }
}
