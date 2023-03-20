package lm.echo;
import java.io.IOException;
import java.net.*;
import java.util.*;


public class Server extends Thread {

    public static int MAX_PLAYERS_NBR = 10;
    
    public List<ClientThread> active_players; 
    
    public String[] words =
        new String[]{
        "Network",
        "HardDrive",
        "Computer",
        "Mouse",
        "Orange",
        "Red",
        "Huge"
    };
    
    //public Map<Integer, String> party_index_to_word;
    
    Server(){
        this.setName("Server-Thread...");
        active_players = new ArrayList<>();
        //this.party_index_to_word = new HashMap<>();
    }
    
    @Override
    public void run(){
        
        
        ServerSocket serv;
        Socket player_socket;

        try {
            serv = new ServerSocket(9090);
            System.out.println("[Server]");
            while(true){

                player_socket = serv.accept();

                if(active_players.size() < 2){

                    ClientThread new_thread = 
                        new ClientThread(player_socket, this);
                    active_players.add(new_thread); 
                    new_thread.start();
                }else{
                    player_socket.getOutputStream().write('n');
                    player_socket.getOutputStream().write('\n');
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public String get_random_word(){
        return this.words [new Random().nextInt(0, this.words.length -1 )].toUpperCase(); 

    }

    public static void main(String[] argv)
        throws IOException{

        new Server().start();
    }
}
