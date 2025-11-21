package com.bank.integra;

public class TempCrap {

    // 1. Hardcoded credentials (пароль в коде)
    private String dbPass = "admin123";

    public void checkUser(String name, String password) {
        // 2. Сравнение строк через == (классическая ошибка)
        if (name == "admin") {
            // 3. System.out.println вместо логгера
            System.out.println("Admin entered!");

            try {
                connectToDb();
            } catch (Exception e) {
                // 4. Пустой catch (игнорирование ошибок)
            }
        }
    }

    public void loop() {
        // 5. Бесконечный цикл, который повесит поток
        while (true) {
            // doing nothing
        }
    }

    private void connectToDb() {
        // emulation
    }
}