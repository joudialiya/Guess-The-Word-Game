package lm.echo;
import java.net.*;
import java.io.*;

public class Client extends Thread{

    private Socket player_socket;
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedReader console_input;
    private String input = null;
    private int remaining_attempts = 10; 
    private char state;
    
    Client(){
        try {
            this.player_socket = new Socket("127.0.0.1", 9090);
            this.remaining_attempts = 10; 

            this.in = new BufferedReader(
                new InputStreamReader(
                    player_socket.getInputStream())); 

            this.out = new BufferedWriter(
                new OutputStreamWriter(
                    player_socket.getOutputStream()));

            this.console_input = new BufferedReader(
                new InputStreamReader(System.in));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            // checking the server a availability
            if (in.readLine().charAt(0) == 'n'){
                System.out.println("The server is full!");
                return;
            }

            System.out.println("Welcome player!");
            System.out.println("Enter \"play\" to start the game.");


            while (input == null || !input.equals("play")){
                System.out.print(">> ");
                input = console_input.readLine();
            }
            /* send the play flag */
            out.write(input);
            out.newLine();
            out.flush();

            while(true){

                System.out.println("\n//////////////////////");
                System.out.println("WORD: "+in.readLine());
                System.out.println("Remaining Attempts: " + remaining_attempts);
                System.out.println("//////////////////////");

                /* 
                * getting a valide input [char]
                */
                input = null;
                
                while(true){
                    System.out.print("\nEnter a character: ");
                    input = console_input.readLine();
                    if (input.length() == 1)
                    break;
                    System.out.println("\tInvalid input [one letter a time]");
                }
                
                out.write(input + "\n");
                out.flush();
                
                state = in.readLine().charAt(0);
                remaining_attempts = Integer.parseInt(in.readLine());

                switch(state){
                
                    case '0':
                        System.out.print("\n\t##Nice geuss!##");
                        break;
                    case '1':
                        System.out.print("\t##The character is already unlocked [dont abuse this feuture tho]##");
                    break;
                    case '2':
                        System.out.print("\t## >.< bad choice ##");
                    break;
                    
                }
                if(state == '3' || state == '4')
                    break;
                
                
            }
            if (state == '4')
                System.out.println("\nMay be next time! >.<");
            else
                System.out.println("\nWe have a winner [Score: "+ remaining_attempts +"]");
            
            player_socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] argv)
        throws IOException{

        new Client().start();
    }
}