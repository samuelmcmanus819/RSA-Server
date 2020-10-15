//Cryptography.java
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

public class Cryptography {
    private String KeyStoreFile;
    private char[] StorePass;
    private char[] KeyPass;
    private String Alias;
    public Cryptography(String file, char[] SP, char[] KP, String alias){
        KeyStoreFile = file;
        StorePass = SP;
        KeyPass = KP;
        Alias = alias;
    }
    /*
    Name: LoadKeyStore
    Purpose: Loads an instance of the java keystore
    Return: An instance of the keystore class
    Author: Samuel McManus
    Date: October 5, 2020
     */
    public KeyStore LoadKeyStore() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        KeyStore ks = KeyStore.getInstance("jks");
        ks.load(new FileInputStream(KeyStoreFile), StorePass);
        return ks;
    }
    /*
    Name: SignText
    Purpose: Signs a message for the client
    Param Filetext: The file to be signed
    Param ks: An instance of the java keystore
    Return: The input file's signature
    Author: Samuel McManus
    Date: October 5, 2020
     */
    public String SignText(String Filetext, KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException, IOException,
            UnrecoverableKeyException, InvalidKeyException, SignatureException {
        //Gets an instance of the signature class
        Signature signature = Signature.getInstance("SHA256WithRSA");
        //Gets the private key of the superkey pair from the keystore
        PrivateKey RSAKey = (PrivateKey) ks.getKey("superkey", KeyPass);
        //Signs the plaintext
        signature.initSign(RSAKey);
        signature.update(Filetext.getBytes());
        byte[] raw = signature.sign();
        //Returns a base64 encoding of the file's signature
        return Base64.getEncoder().encodeToString(raw);
    }
    /*
    Name: EncryptMessage
    Purpose: Encrypts a plaintext and its signature
    Param Plaintext: The plaintext and signature to be encrypted
    Param Cert: The public key certificate to sign the plaintext and signature with
    Return: The encrypted message
    Author: Samuel McManus
    Date: October 5, 2020
     */
    public String EncryptMessage(String Plaintext, Certificate Cert) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        StringBuilder Ciphertext = new StringBuilder();
        //Gets an instance of an RSA cipher
        Cipher RSA = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        RSA.init(Cipher.ENCRYPT_MODE, Cert);
        int offset = 0;
        int MaxLength = 501;
        //Encrypts the file in blocks less than the maximum length for a 4096 bit RSA cipher
        while((offset + MaxLength) < (Plaintext.length() - 1)) {
            byte[] output = RSA.doFinal(Plaintext.getBytes(), offset, offset + MaxLength);
            offset = offset + MaxLength;
            if (output != null)
                Ciphertext.append(new String(output));
            System.out.println(Ciphertext.length());
        }
        //Encrypts the last block of the file
        byte[] output = RSA.doFinal(Plaintext.getBytes(), offset, (Plaintext.length() - offset));
        if(output != null)
            Ciphertext.append(new String(output));
        System.out.println(Plaintext.length());
        System.out.println(Ciphertext.toString().length());
        //Returns the encrypted as a base 64 string
        return Base64.getEncoder().encodeToString(Ciphertext.toString().getBytes());
    }
    /*
    Name: GetCert
    Purpose: Gets a certificate for the superkey's public key
    Param ks: The keystore to get the certificate from
    Return: The public key certificate
    Author: Samuel McManus
    Date: October 5, 2020
     */
    public Certificate GetCert(KeyStore ks) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        return ks.getCertificate(Alias);
    }
}
