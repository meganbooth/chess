package client;

import chess.ChessBoard;
import model.GameData;
import model.result.ListGamesResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PostloginClient implements Client {
    public ServerFacade facade = new ServerFacade(8080);

    private boolean switchForward = false;
    private boolean switchBackward = false;
    private String authToken = null;
    private String selectedColor;
    private static final String HELP_TEXT = """
        Available commands:
          list - list all existing games
          create - create a new game
          join - join an existing game
          observe - watch a game
          logout - exit to login menu
          help - show this menu
        """;

    List<GameData> games;
    Scanner scanner = new Scanner(System.in);

    public PostloginClient(String authToken) {
        this.authToken = authToken;
    }

    public String handleInput(String input) {
        return switch (input) {
            case "help" -> HELP_TEXT;
            case "logout" -> {
                try {
                    facade.logout(authToken);
                } catch (Exception e) {
                    // ignore, proceed regardless
                }
                switchBackward = true;
                yield "See you later!";
            }
            case "observe" -> {
                try{
                    ListGamesResult listResult = facade.listGames(authToken);
                    games = new ArrayList<>(listResult.games());
                    System.out.print("Game Number: ");
                    int gameNumber = Integer.parseInt(scanner.nextLine());
                    int gameID = games.get(gameNumber - 1).gameID();
                    this.selectedColor = "WHITE";
                    switchForward = true;
                    yield "Observing game";
                } catch (Exception e) {
                    yield "Error: could not observe game.";
                }
            }
            case "join" -> {
                try {
                    ListGamesResult listResult = facade.listGames(authToken);
                    games = new ArrayList<>(listResult.games());
                    System.out.print("Game Number: ");
                    int gameNumber = Integer.parseInt(scanner.nextLine());
                    int gameID = games.get(gameNumber - 1).gameID();
                    System.out.print("Color: ");
                    String color = scanner.nextLine();
                    this.selectedColor = color.toUpperCase();
                    facade.joinGame(color.toUpperCase(),gameID,authToken);
                    switchForward = true;
                    yield "Welcome";
                } catch(Exception e) {
                    yield "Error: could not join game.";
                }
            }
            case "create" -> {
                System.out.print("Game Name: ");
                String gameName = scanner.nextLine();
                if (gameName.isBlank()) {
                    yield "Error: game name cannot be empty.";
                }
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
            default -> "Command not recognized.\n" + HELP_TEXT;
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
    public String getColor() {return selectedColor;}
}
