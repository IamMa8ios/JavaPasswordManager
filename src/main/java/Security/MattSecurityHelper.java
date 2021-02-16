package Security;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class MattSecurityHelper {
    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPERCASE = CHAR_LOWERCASE.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static final String OTHER_PUNCTUATION = "!@#&()–[{}]:;',?/*";
    private static final String OTHER_SYMBOL = "~$^+=<>";
    private static final String OTHER_SPECIAL = OTHER_PUNCTUATION + OTHER_SYMBOL;
    private static final String PASSWORD_ALLOW = CHAR_LOWERCASE + CHAR_UPPERCASE + DIGIT + OTHER_SPECIAL;




    //-----------------------------------------------------ENCRYPTION-----------------------------------------------------


    /**
     * Generate AES Key
     */
    public static byte[] createSymmetricKey() {

        KeyGenerator keyGenerator;

        try {

            keyGenerator = KeyGenerator.getInstance("AES"); //Create KeyGenerator for AES Algorithm
            keyGenerator.init(256); //Set key size for KeyGenerator
            SecretKey secretKey = keyGenerator.generateKey(); //Create new Secret Key with KeyGenerator's specs
            return secretKey.getEncoded();

        }catch (NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Encrypt any object with the provided AES Key
     * @param plainText - Object to be encrypted
     * @param secretKey - Secret Key for AES Algorithm
     * @return encrypted Object in byte[] format
     */
    public static byte[] encrypt(Object plainText, SecretKey secretKey) {

        byte[] value=serialize(plainText); //Convert Object to byte[]

        try{
            Cipher cipher = Cipher.getInstance("AES"); //Create Cipher Object for AES Algorithm functionality
            cipher.init(Cipher.ENCRYPT_MODE, secretKey); //Initialize Cipher Specs for AES Encryption

            return cipher.doFinal(value); //Generate Cipher Text in byte[] format

        } catch (InvalidKeyException exception){
            exception.printStackTrace();
        } catch (BadPaddingException exception){
            exception.printStackTrace();
        } catch (IllegalBlockSizeException exception){
            exception.printStackTrace();
        } catch (NoSuchPaddingException exception){
            exception.printStackTrace();
        } catch (NoSuchAlgorithmException exception){
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Decrypt byte[] given with provided AES Key
     * @param cipherText - byte[] to be decrypted
     * @param secretKey - Secret Key for AES Algorithm
     * @return Object result from decryption process
     */
    public static Object decrypt(byte[] cipherText, SecretKey secretKey) {

        try{
            Cipher cipher = Cipher.getInstance("AES"); //Create Cipher Object for AES Algorithm functionality
            cipher.init(Cipher.DECRYPT_MODE,secretKey); //Initialize Cipher Specs for AES decryption

            byte[] value=cipher.doFinal(cipherText); //Decrypt cipherText in byte[] format

            Object plain=deserialize(value); //Convert cipherText in its original Object format

            return plain;

        } catch (InvalidKeyException exception){
            exception.printStackTrace();
        } catch (BadPaddingException exception){
            exception.printStackTrace();
        } catch (IllegalBlockSizeException exception){
            exception.printStackTrace();
        } catch (NoSuchPaddingException exception){
            exception.printStackTrace();
        } catch (NoSuchAlgorithmException exception){
            exception.printStackTrace();
        }
        return null;
    }


    //-----------------------------------------------------ENCRYPTION-----------------------------------------------------

















    //-----------------------------------------------------PASSWORD GENERATOR-----------------------------------------------------


    public static String generateStrongPassword(int size) {

        StringBuilder result = new StringBuilder();

        // at least 2 chars (lowercase)
        String strLowerCase = generateRandomString(CHAR_LOWERCASE, size/5);
        //System.out.format("%-20s: %s%n", "Chars (Lowercase)", strLowerCase);
        result.append(strLowerCase);

        // at least 2 chars (uppercase)
        String strUppercaseCase = generateRandomString(CHAR_UPPERCASE, size/5);
        //System.out.format("%-20s: %s%n", "Chars (Uppercase)", strUppercaseCase);
        result.append(strUppercaseCase);

        // at least 2 digits
        String strDigit = generateRandomString(DIGIT, size/5);
        //System.out.format("%-20s: %s%n", "Digits", strDigit);
        result.append(strDigit);

        // at least 2 special characters (punctuation + symbols)
        String strSpecialChar = generateRandomString(OTHER_SPECIAL, size/5);
        //System.out.format("%-20s: %s%n", "Special chars", strSpecialChar);
        result.append(strSpecialChar);

        // remaining, just random
        String strOther = generateRandomString(PASSWORD_ALLOW, size - (size/5)*4);
        //System.out.format("%-20s: %s%n", "Others", strOther);
        result.append(strOther);

        String password = result.toString();
        // combine all
        //System.out.format("%-20s: %s%n", "Password", password);
        // shuffle again
        //System.out.format("%-20s: %s%n", "Final Password", shuffleString(password));
        //System.out.format("%-20s: %s%n%n", "Password Length", password.length());

        return shuffleString(password);
    }

    // generate a random char[], based on `input`
    private static String generateRandomString(String input, int size) {

        SecureRandom random=new SecureRandom();

        if (input == null || input.length() <= 0)
            throw new IllegalArgumentException("Invalid input.");
        if (size < 1) throw new IllegalArgumentException("Invalid size.");

        StringBuilder result = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            // produce a random order
            int index = random.nextInt(input.length());
            result.append(input.charAt(index));
        }
        return result.toString();
    }

    // for final password, make it more random
    public static String shuffleString(String input) {
        List<String> result = Arrays.asList(input.split(""));
        Collections.shuffle(result);
        // java 8
        return result.stream().collect(Collectors.joining());
    }


    //-----------------------------------------------------PASSWORD GENERATOR-----------------------------------------------------















    //-----------------------------------------------------SERIALIZATION-----------------------------------------------------



    /*
        Supporting methods used for converting Object
        to byte[] and vice versa
        https://stackoverflow.com/questions/3736058/java-object-to-byte-and-byte-to-object-converter-for-tokyo-cabinet/3736091
     */


    public static byte[] serialize(Object obj){
        try {
            ByteArrayOutputStream container = new ByteArrayOutputStream();//empty container that will keep the bytes from conversion
            ObjectOutputStream os = new ObjectOutputStream(container);//instead of file use the container
            os.writeObject(obj);//write to container
            return container.toByteArray();//return byte[]
        } catch (IOException ex) {
            Logger.getLogger(MattSecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Object deserialize(byte[] data){
        ObjectInputStream is = null;
        try {
            ByteArrayInputStream container = new ByteArrayInputStream(data);//container that has the byte[] from a serialized object
            is = new ObjectInputStream(container);//instead of file use the container
            return is.readObject();//return Object, casting is needed according to object that is expected
        } catch (IOException ex) {
            Logger.getLogger(MattSecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MattSecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(MattSecurityHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    //-----------------------------------------------------SERIALIZATION-----------------------------------------------------


}
