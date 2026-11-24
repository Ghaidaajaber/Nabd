package org.example.nabd;

import org.mindrot.jbcrypt.BCrypt;
import java.util.Scanner;

public class PasswordGenerator {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.print("Enter password: ");
        String password = input.nextLine();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        System.out.println("Hashed Password: " + hashedPassword);
        input.close();
    }

}