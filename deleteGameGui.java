import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// THis is the delete games gui
// it allows user to delete selected games

public class deleteGameGui {
    private JFrame deleteGamesFrame;
    private JTextField searchTxt, usernameTxt, passwordTxt;
    private JTextField resultTxt;
    private JTextArea resultArea;
    private JList<String> gamesList;
    private JButton searchBtn, deleteBtn, exitBtn;
    private DefaultListModel<String> gamesListModel;
    List<gameAttributes> gamesStored  = new ArrayList<>();
    List<gameAttributes> searchResults = new ArrayList<>();
    private gameAttributes selectedGame;
    private Runnable mainMenuRunnable;


    String userName = "Login";
    String passWord = "1234";

    public deleteGameGui(List<gameAttributes> gamesStored, Runnable mainMenuRunnable) {
        this.gamesStored = gamesStored;
        this.mainMenuRunnable = mainMenuRunnable;
        buildDeleteGamesGui();
        loadGames();
    }


    // This is the Gui of the add games class
    // I am setting up the basics of the gui
    // such as the size, exiting the gui when i click the X button
    public void buildDeleteGamesGui() {
        deleteGamesFrame = new JFrame("Delete Games");
        deleteGamesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        deleteGamesFrame.setSize(800, 600);
        deleteGamesFrame.setLayout(new BorderLayout(10, 10));

        JPanel deleteGamesPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        //I have created text fields for the user to search for games
        searchTxt = new JTextField();
        searchBtn = new JButton("Search Title");

        // Here I add the labels and the text fields to the frame
        deleteGamesPanel.add(new JLabel("Search Game: "));
        deleteGamesPanel.add(searchTxt);
        deleteGamesPanel.add(new JLabel(""));
        deleteGamesPanel.add(searchBtn);


        deleteGamesPanel.add(new JLabel("Username: "));
        usernameTxt = new JTextField();
        deleteGamesPanel.add(usernameTxt);

        deleteGamesPanel.add(new JLabel("Password: "));
        passwordTxt = new JTextField();
        deleteGamesPanel.add(passwordTxt);

        deleteBtn = new JButton("Delete Game");
        exitBtn = new JButton("Exit");
        deleteGamesPanel.add(deleteBtn);
        deleteGamesPanel.add(exitBtn);

        gamesListModel = new DefaultListModel<>(); // Create a model to store game titles
        gamesList = new JList<>(gamesListModel); // Create a JList component to display
        gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);  // Allow only one game to be selected at a time
        JScrollPane gamesScroller = new JScrollPane(gamesList);  // Make the list scrollable


        resultArea = new JTextArea(5, 30); // Making a text area to display results
        resultArea.setEditable(false); // Makes the text area non-editable
        resultArea.setLineWrap(true); // Wraps text lines that are too long
        resultArea.setWrapStyleWord(true); // Wraps lines at word boundaries
        JScrollPane resultScroller = new JScrollPane(resultArea); // Used to scroll the results

        deleteGamesFrame.add(deleteGamesPanel, BorderLayout.NORTH);
        deleteGamesFrame.add(gamesScroller, BorderLayout.WEST);
        deleteGamesFrame.add(resultScroller, BorderLayout.CENTER);

        // THis is the action listener for the search button
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchForGames();
            }
        });

        // This is the action listener for selecting the game
        gamesList.addListSelectionListener(event -> selectedGame());

        // This is the action listener for deleting the selected game
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelectedGame(userName, passWord);
            }
        });

        // This is the exit action listener
        exitBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitToMainMenu();
            }
        });

        deleteGamesFrame.setLocationRelativeTo(null); // Centers the frame on screen
        deleteGamesFrame.setVisible(true); // Makes the frame visible
    }

    private void searchForGames() {
        String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";
        // Retrieve and change the search text to lowercase
        String searchingTitle = searchTxt.getText().toLowerCase();


        //Clears previous search
        gamesListModel.clear();
        searchResults.clear();

        // Filter gamesStored to find titles that contain the search term,
        // then store matching games in searchResults
        //searchResults = gamesStored.stream().filter(game -> game.getTitle()
                //.toLowerCase().contains(searchingTitle)).collect(Collectors.toList());

        String searchQuery;
            if(searchingTitle.isEmpty()) {
                searchQuery = "SELECT gameId, title, genre, release_date, console, studio, product_type, multiplayer, price FROM add_games";

            } else {
                searchQuery ="SELECT gameId, title, genre, release_date, console, studio, product_type, multiplayer, price FROM add_games WHERE LOWER(title) LIKE ?";
            }


        try(Connection connection = DriverManager.getConnection(dbUrl);
            PreparedStatement state = connection.prepareStatement(searchQuery)){

            if (!searchingTitle.isEmpty()) {
                state.setString(1, "%" + searchingTitle + "%");
            }

            try (ResultSet resultSet = state.executeQuery()){
                while (resultSet.next()) {
                    int gameId = resultSet.getInt("gameId");
                    String title = resultSet.getString("title");
                    String genre = resultSet.getString("genre");
                    String releaseDate = resultSet.getString("release_date");
                    String console = resultSet.getString("console");
                    String studio = resultSet.getString("studio");
                    String productType = resultSet.getString("product_type");
                    String multiplayer = resultSet.getString("multiplayer");
                    double price = resultSet.getDouble("price");

                    gameAttributes game = new gameAttributes(gameId, title, genre, releaseDate, console, studio, productType, multiplayer, price);
                    searchResults.add(game);

                    gamesListModel.addElement(title);
                }
            }

            if(searchResults.isEmpty()) {
                resultArea.setText("No results found");
            } else {
                resultArea.setText("Select a game to Delete");
            }

        } catch (SQLException e) {
           e.printStackTrace();
           resultArea.setText("Something went wrong");
        }

        /*
        // Clear the current list model before adding new results
        gamesListModel.clear();

        // Add each found game's title to gamesListModel
        for (gameAttributes game : searchResults) {
            gamesListModel.addElement(game.getTitle());
        }
        // If no games match the search, show a "No game found" message
        if (searchResults.isEmpty()) {
            resultArea.setText("No game found");
        } else {
            resultArea.setText("Select a game to delete");
        }

         */
    }

    private void selectedGame() {
        // Get the index of the selected game
        int selectedIndex = gamesList.getSelectedIndex();

        // Check if game is valid
        if (selectedIndex >= 0 && selectedIndex < searchResults.size()) {
            selectedGame = searchResults.get(selectedIndex);
            resultArea.setText("Selected Game: " + selectedGame.getTitle() + "\nAre you sure you want to delete this game?");
        }
    }
    /*

            // Prompt for username and password
            String userName2 = JOptionPane.showInputDialog("Enter Username: ");
            String passWord2 = JOptionPane.showInputDialog("Enter Password: ");

            // Check if the user canceled either input dialog
            if (userName2 == null || passWord2 == null) {
                resultArea.setText("Deletion canceled. You may select another game to delete.");
            } else {
                // Proceed to delete if both inputs are given
                deleteSelectedGame(userName2, passWord2);
            }
        }
    }

     */

    private void deleteSelectedGame(String userName2, String passWord2) {
        String userNameMatch = JOptionPane.showInputDialog("Enter Username: ");
        String passWordMatch = JOptionPane.showInputDialog("Enter Password: ");

        if (selectedGame != null) {
            // Verify user credentials
            if (userName.equals(userNameMatch) && passWord.equals(passWordMatch)) {
                String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";
                String deleteQuery = "DELETE FROM add_games WHERE gameId = ?";

                try (Connection connection = DriverManager.getConnection(dbUrl);
                     PreparedStatement state = connection.prepareStatement(deleteQuery)) {


                    state.setInt(1, selectedGame.getGameid());

                    int rowsAffected = state.executeUpdate();
                    if (rowsAffected > 0) {
                        // Remove the game from in-memory lists and update UI
                        searchResults.remove(selectedGame);
                        gamesListModel.removeElement(selectedGame.getTitle());
                        resultArea.setText("The game \"" + selectedGame.getTitle() + "\" has been deleted from the database.");

                        // Clear the selected game
                        selectedGame = null;
                    } else {
                        resultArea.setText("Failed to delete the game from the database.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    resultArea.setText("Error deleting game from the database: " + e.getMessage());
                }
            } else {
                resultArea.setText("Incorrect Username or Password");
            }
        } else {
            resultArea.setText("Select a game to delete");
        }
    }

    // This is to exit this class
    private void exitToMainMenu() {
        deleteGamesFrame.dispose();
        if (mainMenuRunnable != null) {
            mainMenuRunnable.run();
        }
    }

    private void loadGames() {
        searchTxt.setText("");
        searchForGames();
        /*
        String dbUrl = "jdbc:sqlite:C:/Sqlite/mydbb.db";
        String selectQuery = "SELECT gameId, title, genre, release_date, console, studio, product_type,multiplayer, price FROM add_games";
        gamesStored.clear();

        try (Connection connection = DriverManager.getConnection(dbUrl);
             PreparedStatement state = connection.prepareStatement(selectQuery);
             ResultSet resultSet = state.executeQuery()) {

            while (resultSet.next()) {
                int gameId = resultSet.getInt("gameId");
                String title = resultSet.getString("title");
                String genre = resultSet.getString("genre");
                String releaseDate = resultSet.getString("release_date");
                String console = resultSet.getString("console");
                String studio = resultSet.getString("studio");
                String productType = resultSet.getString("product_type");
                String multiplayer = resultSet.getString("multiplayer");
                double price = resultSet.getDouble("price");

                gameAttributes game = new gameAttributes(gameId, title, genre, releaseDate, console, studio, productType, multiplayer, price);
                gamesStored.add(game);

                System.out.println("Games from database" + game);
            }

            if (gamesStored.isEmpty()) {
                System.out.println("No game found");
                    if(resultArea != null) {
                        resultArea.setText("No game found");
                    }

                } else {
                    System.out.println("Games loaded from database");
                    resultArea.setText("Games loaded from database");
                }
        } catch (SQLException e) {
            e.printStackTrace();
            if(resultArea != null) {
                resultArea.setText("Error loading games from database: " + e.getMessage());
            }
        }

         */
    }
}