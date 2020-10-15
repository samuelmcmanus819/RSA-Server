//Networking.java
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.concurrent.Semaphore;

public class Networking extends Thread {
    private Socket io;
    private Semaphore Sema;
    private Cryptography MyCryptographyInfo;

    public Networking(Socket i, Semaphore Sema, Cryptography MyCrypto){
        io = i;
        this.Sema = Sema;
        MyCryptographyInfo = MyCrypto;
    }

    /*
    Name: Listen
    Purpose: Listens for connecting users
    Author: Samuel McManus
    Date: October 4, 2020
     */
    public void run(){
        try{
            String Filetext;
            //Creates input and output streams
            ObjectInputStream Input = new ObjectInputStream(io.getInputStream());
            ObjectOutputStream Output = new ObjectOutputStream(io.getOutputStream());
            //Load the keystore
            KeyStore ks = MyCryptographyInfo.LoadKeyStore();
            //Gets the servers public key and sends it to the client
            java.security.cert.Certificate MyCert = MyCryptographyInfo.GetCert(ks);
            Output.writeObject(MyCert);
            //Gets the client's public key certificate
            Certificate ClientCert = (Certificate) Input.readObject();
            while(true){
                String Filename = (String)Input.readObject();
                Filetext = IO.ReadFile(Filename);
                if(!Filetext.equals("")){
                    Output.writeObject("Good");
                    break;
                }
                else{
                    Output.writeObject("Bad");
                }
            }
            //Sign the file and send it to the user
            String Signature = MyCryptographyInfo.SignText(Filetext, ks);
            Output.writeObject(Signature);
            Output.writeObject(Filetext);
            Output.close();
            Input.close();
            io.close();
        } catch (IOException | ClassNotFoundException | KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
    }
}
