/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreamlex.clientauth;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RSAEncryptionManager {
    public static final int KEY_SIZE_DEFAULT = 1024;
    
    public static void saveKeyPair(KeyPair keyPair) {
        try {
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            FileOutputStream fos = new FileOutputStream(PUBLICK_KEY_PATH);
            
            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();
            
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            
            fos = new FileOutputStream(PRIVATE_KEY_PATH);
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
        } catch (IOException ex) {
            Logger.getLogger(RSAEncryptionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public static KeyPair loadKeyPair() {
        return new KeyPair(loadPublicKey(), loadPrivateKey());
    }
    
    public static PrivateKey loadPrivateKey() {
        try {            
            /*PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readFile(PRIVATE_KEY_PATH));
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            
            return kf.generatePrivate(spec);*/
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readFile(PRIVATE_KEY_PATH));
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);

            PrivateKey priv = kf.generatePrivate(spec);

            RSAPrivateCrtKeySpec pkSpec = kf.getKeySpec(priv, RSAPrivateCrtKeySpec.class);
            System.out.println("Prime exponent p : " + pkSpec.getPrimeExponentP());
            System.out.println("Prime exponent q : " + pkSpec.getPrimeExponentQ());
            System.out.println("Modulus : " + pkSpec.getModulus().toString(16));
            System.out.println("Private exponent : " + pkSpec.getPrivateExponent().toString(16));
            System.out.println("Public exponent : " + pkSpec.getPublicExponent().toString(16));

            return priv;
        } catch (Exception ex) {
            Logger.getLogger(RSAEncryptionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static PublicKey loadPublicKey()  {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(readFile(PUBLICK_KEY_PATH));
		
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception ex) {
            Logger.getLogger(RSAEncryptionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public static KeyPair generateKeyPair(int keySize) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(RSAEncryptionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(keyGen != null) 
        {
            keyGen.initialize(KEY_SIZE_DEFAULT);
            
            return keyGen.generateKeyPair();
        }
        else return null;
    }
    
    private static byte[] readFile(String path) throws FileNotFoundException, IOException {
        File filePublicKey = new File(path);
        FileInputStream fis = new FileInputStream(path);
        
        byte[] key = new byte[(int) filePublicKey.length()];
        
        fis.read(key);
        fis.close();
        
        return key;
    }
    
    private static final String PUBLICK_KEY_PATH = "public.key";
    private static final String PRIVATE_KEY_PATH = "private.key";
    private static final String ALGORITHM = "RSA";
    
    
}
