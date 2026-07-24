package client;

import model.GameData;
import model.result.CreateGameResult;
import model.result.ListGamesResult;
import model.result.LoginResult;
import model.result.RegisterResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostloginClient implements Client {
    public ServerFacade facade = new ServerFacade(8080);

    private boolean switchForward = false;
    private boolean switchBackward = false;
    private String authToken = null;

    List<GameData> games;
    Scanner scanner = new Scanner(System.in);

    public PostloginClient(String authToken) {
        this.authToken = authToken;
    }

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> """
                    Available commands:
                      list - list all existing games
                      create - create a new game
                      join - join an existing game
                      observe - watch a game
                      logout - exit to login menu
                      help - show this menu
                    """;
            case "logout" -> {
                try {
                    facade.logout(authToken);
                } catch (Exception e) {
                    // ignore, proceed regardless
                }
                switchBackward = true;
                yield "See you later!";
            }
            case "observe" -> "observe";
            case "join" -> {
                try {
                    ListGamesResult listResult = facade.listGames(authToken);
                    games = new ArrayList<>(listResult.games());
                    System.out.print("Game Number: ");
                    int gameNumber = Integer.parseInt(scanner.nextLine());
                    int gameID = games.get(gameNumber - 1).gameID();
                    System.out.print("Color: ");
                    String color = scanner.nextLine();
                    facade.joinGame(color.toUpperCase(),gameID,authToken);
                    yield "Welcome";
                } catch(Exception e) {
                    yield "Error: could not join game.";
                }
            }
            case "create" -> {
                System.out.print("Game Name: ");
                String gameName = scanner.nextLine();
                try {
                    facade.createGame(gameName,authToken);
                    yield "Game created successfully";
                } catch(Exception e) {
                    yield "Error: Game not created.";
                }
            }
            case "list" -> {
                try {
                    ListGamesResult result = facade.listGames(authToken);
                    games = new ArrayList<>(result.games());
                    String gameList = "";
                    for (int i = 0; i < games.size(); i++) {
                        gameList += (i + 1) + ". " + games.get(i).gameName()
                                + ", white:"
                                + (games.get(i).whiteUsername() != null ? games.get(i).whiteUsername() : "open")
                                + ", black:"
                                + (games.get(i).blackUsername() != null ? games.get(i).blackUsername() : "open")
                                + "\n";
                    }
                    yield gameList;
                } catch(Exception e) {
                    yield "Error: unauthorized";
                }
            }
            default -> """
                    Command not recognized.
                    Available commands:
                      list - list all existing games
                      create - create a new game
                      join - join an existing game
                      observe - watch a game
                      logout - exit to login menu
                      help - show this menu
                    """;
        };
    }

    public boolean shouldSwitchForward() {
        return switchForward;
    }
    public boolean shouldSwitchBackward() {
        return switchBackward;
    }
    public String getAuthToken() {
        return authToken;
    }
}
