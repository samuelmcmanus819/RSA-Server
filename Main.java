//Main.java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String []args){
        int CountClients = 1;
        //Set the keystore data from commandline entries
        String ksf = args[0];
        char[] sp = new char[args[1].length()];
        args[2].getChars(0, args[1].length(), sp, 0);
        char[] kp = new char[args[2].length()];
        args[2].getChars(0, args[2].length(), kp, 0);
        String alias = args[3];

        //Create a cryptography object from the user-given info
        Cryptography MyCryptoInfo = new Cryptography(ksf, sp, kp, alias);
        try{
            //Creates a server socket listening on port 6622 with a queue of 8
            ServerSocket serverSocket = new ServerSocket(6622, 8);
            //Accept as many clients as needed
            while(true) {
                Socket ClientServer = serverSocket.accept();
                Semaphore Sema = new Semaphore(1);
                System.out.println("Starting client " + CountClients);
                new Networking(ClientServer, Sema, MyCryptoInfo).start();
                CountClients++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
