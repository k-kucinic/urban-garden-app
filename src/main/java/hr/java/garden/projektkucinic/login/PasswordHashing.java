package hr.java.garden.projektkucinic.login;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {


    public static String getHashedPassword(String passwordNormal) {
        return BCrypt.hashpw(passwordNormal, BCrypt.gensalt());
    }

    public static boolean checkIfPasswordIsCorrect(String passwordNormal, String hashedPassword) {
        return BCrypt.checkpw(passwordNormal, hashedPassword);
    }
}
