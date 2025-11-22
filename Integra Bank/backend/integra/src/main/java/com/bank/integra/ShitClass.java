package com.bank.integra;

public class ShitClass {

    private String dbPass = "admin123";

    public void checkUser(String name, String password) {
        if (name == "admin") {
            // 3. System.out.println вместо логгера
            System.out.println("Admin entered!");

            try {
                connectToDb();
            } catch (Exception e) {
            }
        }
    }

    public void loop() {
        while (true) {

        }
    }

    private void connectToDb() {
        
    }
}
