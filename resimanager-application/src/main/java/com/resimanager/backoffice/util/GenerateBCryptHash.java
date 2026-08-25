package com.resimanager.backoffice.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenerateBCryptHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Map of user ID -> password
        Map<String, String[]> users = new LinkedHashMap<>();
        users.put("1", new String[]{"admin", "Admin2024!"});
        users.put("2", new String[]{"cmartinez", "Carlos2024!"});
        users.put("3", new String[]{"mrodriguez", "Maria2024!"});
        users.put("4", new String[]{"jperez", "Juan2024!"});
        users.put("5", new String[]{"agarcia", "Ana2024!"});
        users.put("6", new String[]{"lgomez", "Luis2024!"});
        
        System.out.println("===============================================");
        System.out.println("BCRYPT HASHES FOR ALL USERS");
        System.out.println("===============================================\n");
        
        for (Map.Entry<String, String[]> entry : users.entrySet()) {
            String id = entry.getKey();
            String username = entry.getValue()[0];
            String password = entry.getValue()[1];
            String hash = encoder.encode(password);
            
            System.out.println("-- User ID: " + id + " (" + username + ")");
            System.out.println("-- Password: " + password);
            System.out.println("-- Hash: " + hash);
            System.out.println("-- Verified: " + encoder.matches(password, hash));
            System.out.println();
        }
        
        System.out.println("===============================================");
        System.out.println("FORMATTED FOR SQL");
        System.out.println("===============================================\n");
        
        for (Map.Entry<String, String[]> entry : users.entrySet()) {
            String id = entry.getKey();
            String username = entry.getValue()[0];
            String password = entry.getValue()[1];
            String hash = encoder.encode(password);
            
            System.out.println("-- " + username + ": " + password);
            System.out.println("'" + hash + "',  -- ID: " + id);
        }
    }
}
