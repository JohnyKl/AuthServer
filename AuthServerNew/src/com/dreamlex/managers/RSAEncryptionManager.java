/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dreamlex.managers;

import com.dreamlex.DebugLog;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncryptionManager {
    public static final int KEY_SIZE_DEFAULT = 1024;

    //private static final org.slf4j.Logger log = LoggerFactory.getLogger(RSAEncryptionManager.class);

    public static void saveKeyPair(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        savePrivateKey(privateKey, PRIVATE_KEY_PATH);
        savePublicKey(publicKey, PUBLIC_KEY_PATH);
    }

    private static void savePublicKey(PublicKey key, String path) {
        try{
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key.getEncoded());

            FileOutputStream fos = new FileOutputStream(path);

            fos.write(x509EncodedKeySpec.getEncoded());
            fos.close();
        }
        catch(Exception e) {
            DebugLog.error(e.getMessage());
        }
    }

    private static void savePrivateKey(PrivateKey key, String path) {
        try{
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(key.getEncoded());

            FileOutputStream fos = new FileOutputStream(path);
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
        }
        catch(Exception e) {
            DebugLog.error(e.getMessage());
        }
    }
        
    public static KeyPair loadKeyPair() {
        return new KeyPair(loadPublicKey(), loadPrivateKey(PRIVATE_KEY_PATH));
    }

    public static PrivateKey loadPrivateKey() {
        return loadPrivateKey(PRIVATE_KEY_PATH);
    }

    public static PrivateKey loadPrivateKey(String filePath) {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(readFile(filePath));
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);

            PrivateKey priv = kf.generatePrivate(spec);

            /*RSAPrivateCrtKeySpec pkSpec = kf.getKeySpec(priv, RSAPrivateCrtKeySpec.class);
            DebugLog.debug("Prime exponent p : " + pkSpec.getPrimeExponentP());
            DebugLog.debug("Prime exponent q : " + pkSpec.getPrimeExponentQ());
            DebugLog.debug("Modulus : " + pkSpec.getModulus().toString(16));
            DebugLog.debug("Private exponent : " + pkSpec.getPrivateExponent().toString(16));
            DebugLog.debug("Public exponent : " + pkSpec.getPublicExponent().toString(16));
*/
            return priv;
        } catch (Exception ex) {
            DebugLog.error(ex.getMessage());
        }
        
        return null;
    }

    public static void generatePublicKey(String privateKeyPath, String publicKeyPath){
        PrivateKey priv = loadPrivateKey(privateKeyPath);
        try {
            RSAPrivateCrtKey rsaCrtKey = (RSAPrivateCrtKey) priv;

            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(rsaCrtKey.getModulus(), rsaCrtKey.getPublicExponent());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            savePublicKey(publicKey, publicKeyPath);

            DebugLog.debug("Generated public key was save at " + publicKeyPath + " :\n" + publicKey.toString());
        } catch (Exception e) {
            DebugLog.error(e.getMessage());
        }
    }
    
    public static PublicKey loadPublicKey()  {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(readFile(PUBLIC_KEY_PATH));
		
            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception ex) {
            DebugLog.error(ex.getMessage());
        }
        
        return null;
    }
    
    public static KeyPair generateKeyPair(int keySize) {
        KeyPairGenerator keyGen = null;
        try {
            keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            DebugLog.error(ex.getMessage());
        }
        
        if(keyGen != null) 
        {
            keyGen.initialize(KEY_SIZE_DEFAULT);
            
            return keyGen.generateKeyPair();
        }
        else return null;
    }
    
    private static byte[] readFile(String path) throws IOException {
        File filePublicKey = new File(path);
        FileInputStream fis = new FileInputStream(path);
        
        byte[] key = new byte[(int) filePublicKey.length()];
        
        fis.read(key);
        fis.close();
        
        return key;
    }
    
    private static final String PUBLIC_KEY_PATH = "public.key";
    private static final String PRIVATE_KEY_PATH = "private.key";
    private static final String ALGORITHM = "RSA";
    
    
}
