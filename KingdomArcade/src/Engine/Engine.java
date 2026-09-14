package Engine;

import java.util.*;

public class Engine{
    public void animateText(String input, int delay){
        for(char c: input.toCharArray()){
            System.out.println(c);
            try{
                Thread.sleep(delay);
            }catch(InterruptedException e){
            }
        }
        System.out.println();
    }

    public void pressEnter(Scanner scan){
    }
}