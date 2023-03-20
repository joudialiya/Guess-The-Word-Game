package lm.echo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class ClientThread extends Thread{

    private Socket player_socket = null;
    private Server server = null;
    private int remaining_attempts;
    private String word ;
    private boolean[] unlocked_letters;
    private int unlocked_char_count;

    private BufferedReader in;
    private BufferedWriter out;

    public ClientThread(Socket socket, Server server){

        try{

            this.player_socket = socket;
            this.server = server;
            this.remaining_attempts = 10;
            this.word = server.get_random_word();
            this.unlocked_letters = new boolean[word.length()];
            this.unlocked_letters[0] = true;
            this.unlocked_letters[word.length()-1] = true;
            this.unlocked_char_count = 2;
            
            this.in = new BufferedReader(
                new InputStreamReader(
                    player_socket.getInputStream())); 
            
            this.out = new BufferedWriter(
                new OutputStreamWriter(
                    player_socket.getOutputStream()));

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    
    @Override
    public void run(){
        
        try{

            System.out.println("Log: " + Thread.currentThread().getName()+" : Start");
            out.write("y\n");
            out.flush();

            in.readLine();
            this.play();
               
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            System.out.println("Log: " + Thread.currentThread().getName() + " Closed");
            server.active_players.remove(this);
        }
    }
    
    private String hidden_str(String str, boolean[] unlocked){
        char[] hidden =  new char[str.length()];
        for(int i=0; i<str.length(); ++i){
            hidden[i] = (unlocked[i])? str.charAt(i): '-';
        }
        return new String(hidden);
    }

    private void play() throws IOException{

        System.out.println("Log: Session start!");
        int state = 4 ; 
        
        while(this.remaining_attempts != 0){
            /* 
             * send information about the party state
             */
            out.write(hidden_str(word, unlocked_letters) + "\n");
            out.flush();

            
            
            char request = in.readLine().toUpperCase().charAt(0);
            state = this.evaluate(request);
            
            out.write(Integer.valueOf(state).toString() + "\n");
            out.write(Integer.valueOf(this.remaining_attempts).toString() + "\n");
            out.flush();

            if(state == 3 || state == 4)
                return;
        }
    }

    /* 
     * 0: the player unlock a new letter
     * 1: the player requested char is already unlocked
     * 2: the char is not good 
     * 3: player wins [all characters unlocked];
     * 4: player loses 
     */

    private int evaluate(char request){

        for (int i=0; i<word.length(); ++i){
            // char belong to word
            if(word.charAt(i) == request){
                // char already unlocked  
                if(unlocked_letters[i]){
                    return 1;
                // new char unlocked
                }else{
                    // go though the string for potential reappearance of the char
                    for (; i<word.length()-1; ++i){
                        if(word.charAt(i) == request){
                            unlocked_letters[i] = true;
                            ++unlocked_char_count;
                        }
                    }
                    return (this.unlocked_char_count == this.word.length())? 3 : 0;
                }
            }
        }
        --this.remaining_attempts;
        return (this.remaining_attempts == 0)? 4:2;
        
    }
    
}