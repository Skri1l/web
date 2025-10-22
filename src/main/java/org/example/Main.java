package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print("Input smthg ");
                if (!sc.hasNext())
                    break;
                String op = sc.next().toUpperCase(Locale.ROOT);

                if (op.equals("EXIT"))
                    break;

                try {
                    switch (op) {
                        case "GET" -> { // либо ALL, либо id
                            if (!sc.hasNext()) { System.out.println("incorrect input");
                                continue;
                            }
                            String nxt = sc.next();
                            if (nxt.equalsIgnoreCase("ALL")) {
                                exec(HttpRequest.newBuilder(URI.create(BASE_URL))
                                        .GET().timeout(Duration.ofSeconds(20)).build());
                            } else {
                                int id = Integer.parseInt(nxt);
                                if (id <= 0) { System.out.println("incorrect input");
                                    continue;
                                }
                                exec(HttpRequest.newBuilder(URI.create(BASE_URL + id))
                                        .GET().timeout(Duration.ofSeconds(20)).build());
                            }
                        }

                        case "DELETE" -> {
                            if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                continue;
                            }
                            int id = sc.nextInt();
                            if (id <= 0) { System.out.println("incorrect input");
                                continue;
                            }
                            sc.nextLine();

                            HttpResponse<String> resp = client.send(
                                    HttpRequest.newBuilder(URI.create(BASE_URL + id))
                                            .DELETE()
                                            .timeout(Duration.ofSeconds(5))
                                            .build(),
                                    HttpResponse.BodyHandlers.ofString()
                            );

                            if (resp.statusCode() == 200 || resp.statusCode() == 204) {
                                System.out.println("Deleted id " + id);
                            } else {
                                System.out.println("HTTP " + resp.statusCode());
                                System.out.println(resp.body());
                            }
                        }

                        case "POST" -> {
                            System.out.println("Choose randomly? 1 - yes, 2 - no");
                            if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                if (sc.hasNextLine())
                                    sc.nextLine();
                                break;
                            }
                            int answer = sc.nextInt();
                            sc.nextLine(); // съесть \n

                            if (answer == 1) {
                                String json = """
                                        {
                                          "userId": 1,
                                          "title": "New TODO via Java HttpClient",
                                          "completed": false
                                        }
                                        """;
                                exec(HttpRequest.newBuilder(URI.create(BASE_URL))
                                        .header("Content-Type", "application/json; charset=UTF-8")
                                        .POST(HttpRequest.BodyPublishers.ofString(json))
                                        .timeout(Duration.ofSeconds(5))
                                        .build());
                            } else if (answer == 2) {
                                System.out.println("input user_id");
                                if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                    if (sc.hasNextLine())
                                        sc.nextLine();
                                    break;
                                }
                                int userId = sc.nextInt();
                                sc.nextLine();

                                System.out.println("input title");
                                String title = sc.nextLine();

                                System.out.println("completed? (true/false)");
                                if (!sc.hasNextBoolean()) { System.out.println("incorrect input");
                                    if (sc.hasNextLine())
                                        sc.nextLine();
                                    break;
                                }
                                boolean completed = sc.nextBoolean();
                                sc.nextLine();

                                String json = """
                                        {
                                          "userId": %d,
                                          "title": "%s",
                                          "completed": %b
                                        }
                                        """.formatted(userId, escapeJson(title), completed);

                                exec(HttpRequest.newBuilder(URI.create(BASE_URL))
                                        .header("Content-Type", "application/json; charset=UTF-8")
                                        .POST(HttpRequest.BodyPublishers.ofString(json))
                                        .timeout(Duration.ofSeconds(5))
                                        .build());
                            } else {
                                System.out.println("incorrect input");
                            }
                        }

                        case "PUT" -> {
                            System.out.println("Choose randomly? 1 - yes, 2 - no");
                            if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                if (sc.hasNextLine())
                                    sc.nextLine();
                                break;
                            }
                            int answer = sc.nextInt();
                            sc.nextLine(); // съесть \n

                            int id;
                            int userId;
                            String title;
                            boolean completed;
                            String json;

                            if (answer == 1) {
                                System.out.println("input id");
                                if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                    if (sc.hasNextLine())
                                        sc.nextLine();
                                    break;
                                }
                                id = sc.nextInt();
                                sc.nextLine();
                                if (id <= 0) { System.out.println("incorrect input"); break; }

                                userId = 1 + (int)(Math.random() * 10);
                                title = "Updated TODO #" + (100 + (int)(Math.random() * 900));
                                completed = Math.random() < 0.5;

                                json = """
                                        {
                                          "id": %d,
                                          "userId": %d,
                                          "title": "%s",
                                          "completed": %b
                                        }
                                        """.formatted(id, userId, escapeJson(title), completed);

                            } else if (answer == 2) {
                                System.out.println("input id");
                                if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                    if (sc.hasNextLine())
                                        sc.nextLine();
                                    break;
                                }
                                id = sc.nextInt();
                                sc.nextLine();
                                if (id <= 0) { System.out.println("incorrect input");
                                    break;
                                }

                                System.out.println("input user_id");
                                if (!sc.hasNextInt()) { System.out.println("incorrect input");
                                    if (sc.hasNextLine())
                                        sc.nextLine();
                                    break;
                                }
                                userId = sc.nextInt();
                                sc.nextLine();

                                System.out.println("input title");
                                title = sc.nextLine();

                                System.out.println("completed? (true/false)");
                                if (!sc.hasNextBoolean()) { System.out.println("incorrect input");
                                    if (sc.hasNextLine())
                                        sc.nextLine();
                                    break;
                                }
                                completed = sc.nextBoolean();
                                sc.nextLine();

                                json = """
                                        {
                                          "id": %d,
                                          "userId": %d,
                                          "title": "%s",
                                          "completed": %b
                                        }
                                        """.formatted(id, userId, escapeJson(title), completed);

                            } else {
                                System.out.println("incorrect input");
                                break;
                            }

                            exec(HttpRequest.newBuilder(URI.create(BASE_URL + id))
                                    .header("Content-Type", "application/json; charset=UTF-8")
                                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                                    .timeout(Duration.ofSeconds(5))
                                    .build());
                        }

                        default -> System.out.println("incorrect input");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("incorrect input");
                } catch (IOException | InterruptedException e) {
                    System.out.println("incorrect input" + e.getMessage());
                }

                if (sc.hasNextLine())
                    sc.nextLine();
            }
        }
    }

    private void exec(HttpRequest req) throws IOException, InterruptedException {
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        System.out.println("HTTP " + resp.statusCode());
        System.out.println(resp.headers().map());
        System.out.println(resp.body());
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
