package org.example;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/todos/";
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        Commands processor = new Commands(BASE_URL, client);

        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Input smthg ");
                if (!sc.hasNext())
                    break;

                String op = sc.next().toUpperCase(Locale.ROOT);
                if (op.equals("EXIT"))
                    break;

                try {
                    processor.handle(op, sc);
                } catch (NumberFormatException e) {
                    System.out.println("incorrect input");
                } catch (IOException | InterruptedException e) {
                    System.out.println("incorrect input" + e.getMessage());
                }

                if (sc.hasNextLine()) sc.nextLine();
            }
        }
    }
}
