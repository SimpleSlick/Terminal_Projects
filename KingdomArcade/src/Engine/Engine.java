package Engine;

import java.util.*;

public class Engine{
    public void animateText(String input, int delay, int width){
        String[] words = input.split("\\s+");
        int currentLength = 0;

        for(String word: words){
            if(currentLength + word.length() + 1 > width){
                System.out.println();
                currentLength = 0;
            }

            if(currentLength > 0){
                System.out.print(" ");
                currentLength++;
            }

            for(char c: word.toCharArray()){
                System.out.print(c);

                try{
                    Thread.sleep(delay);
                }catch (InterruptedException e){
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            currentLength += word.length();
        }
        System.out.println();
    }


    public void animateMidLine(String input, int delay){
        for(char c : input.toCharArray()){
            System.out.print(c);

            try{
                Thread.sleep(delay);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public void animateCentered(String input, int width, int delay){
        int padding = (width - input.length()) / 2;

        System.out.print(" ".repeat(Math.max(0, padding)));
        animateMidLine(input, delay);
        System.out.println();
    }

    public void animateDivider(int width, int delay, boolean mainHeader){
        String divider = mainHeader ? "=" : "-";
        animateMidLine(divider.repeat(width), delay);
        System.out.println();
    }
}