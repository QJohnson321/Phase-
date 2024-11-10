import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// This Gui allows the user to purchase video games
// the user searches for the game by title and
// selects the game once the game is selected
// then the user can check out the game

public class buyGamesGui {
        private JFrame buyGamesFrame;
        private JTextField titleTxt;
        private JTextArea resultArea, cartArea;
        private JList<String> gamesList;
        private DefaultListModel<String> gamesListModel;
        private List<gameAttributes> gamesStored;

        List<gameAttributes> searchResults = new ArrayList<>();

        private List<cartSetup> userCart = new ArrayList<>();
        private gameAttributes selectedGame;
        private Runnable mainMenuRunnable;

        public buyGamesGui(List<gameAttributes> gamesStored, Runnable mainMenuRunnable) {
            this.gamesStored = gamesStored;
            this.mainMenuRunnable = mainMenuRunnable;
            //buildBuyGamesGui();
        }

        public void buildBuyGamesGui() {
            buyGamesFrame = new JFrame();
            buyGamesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            buyGamesFrame.setSize(800, 600);
            buyGamesFrame.setLayout(new BorderLayout(10,10));

            JPanel buyGamesPanel = new JPanel();
            buyGamesPanel.setLayout(new GridLayout(6,2,10,10));

            buyGamesPanel.add(new JLabel("Title:"));
            titleTxt = new JTextField();
            buyGamesPanel.add(titleTxt);

            JButton searchGamesButton = new JButton("Search Game");
            searchGamesButton.addActionListener(e -> searchForGames());
            buyGamesPanel.add(searchGamesButton);

            JButton addGameButton = new JButton("Add Game");
            addGameButton.addActionListener(e -> addToCart());
            buyGamesPanel.add(addGameButton);

            JButton viewCartButton = new JButton("View Cart");
            viewCartButton.addActionListener(e -> viewCart());
            buyGamesPanel.add(viewCartButton);

            JButton checkoutButton = new JButton("Checkout Cart");
            checkoutButton.addActionListener(e -> checkoutFinal());
            buyGamesPanel.add(checkoutButton);

            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> exitToMainMenu());
            buyGamesPanel.add(exitButton);

            gamesListModel = new DefaultListModel<>();
            gamesList = new JList<>(gamesListModel);
            gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane gamesListScroller = new JScrollPane(gamesList);

            resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setLineWrap(true);
            resultArea.setWrapStyleWord(true);

            buyGamesFrame.add(buyGamesPanel, BorderLayout.NORTH);
            buyGamesFrame.add(gamesListScroller, BorderLayout.WEST);
            buyGamesFrame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
            buyGamesFrame.setLocationRelativeTo(null);
            buyGamesFrame.setVisible(true);

        }

        private void searchForGames() {
            resultArea.setText("");
            String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";

            // Get and lowercase the search term from titleTxt
            String gamesToBuy = titleTxt.getText().toLowerCase();
            //List<gameAttributes> searchResults = new ArrayList<>();

            gamesListModel.clear();
            searchResults.clear();

            String searchQuery = "SELECT * FROM add_games WHERE LOWER(title) LIKE ?";
            try(Connection connection = DriverManager.getConnection(dbUrl);
                PreparedStatement state = connection.prepareStatement(searchQuery)){

                state.setString(1, "%" + gamesToBuy + "%");
                try(ResultSet resultSet = state.executeQuery()){

                    while (resultSet.next()) {
                        int gameId = resultSet.getInt("gameId");
                        String title = resultSet.getString("title");
                        double price = resultSet.getDouble("price");

                        gameAttributes game = new gameAttributes(gameId, title, price);
                        searchResults.add(game);
                        gamesListModel.addElement(title);

                    }
                }
                    if(searchResults.isEmpty()){
                        resultArea.setText("No games found");
                    }else{
                        resultArea.setText("Found " + searchResults.size() + " games");
                    }

            } catch (SQLException e) {
                e.printStackTrace();
                resultArea.setText("Error getting games " + e.getMessage());
            }
/*
            // Loop through all stored games to find titles
                for(gameAttributes myGame : gamesStored) {
                    if(myGame.getTitle().toLowerCase().contains(gamesToBuy)) {
                        searchResults.add(myGame);
                    }
                }

                gamesListModel.clear();// Clear previous search results

                if(searchResults.isEmpty()) {
                    resultArea.append("No games found");
                }else {
                    for(gameAttributes myGame : searchResults) {
                        // Add each found game title to gamesListModel
                        gamesListModel.addElement(myGame.getTitle());

                    }
                    resultArea.append("Found " + searchResults.size() + " games and add them to your cart");
                }

 */
            }
            private void addToCart() {
                resultArea.setText("");
                int selectedIndex = gamesList.getSelectedIndex();  // Get the index of the selected game from the games list

                // Check if the selected index is valid
                if(selectedIndex >= 0 && selectedIndex < searchResults.size()) {
                    selectedGame = searchResults.get(selectedIndex); // Retrieve the selected game from gamesStored
                    String copiesStr = JOptionPane.showInputDialog("Enter copies"); // Asks user to enter quantity

                    try {
                        // Proceeds when the quantity is above 0
                        int copies = Integer.parseInt(copiesStr);
                        if (copies > 0) {
                            boolean cartReady = false;  // Track if game is already in the cart
                            for (cartSetup cartItem : userCart) {
                                if(cartItem.getTitle().equalsIgnoreCase(selectedGame.getTitle())){
                                    cartItem.setQuantity(cartItem.getQuantity() + copies);
                                    cartReady = true;
                                    break;
                                }
                            }
                            // If the game was not already in the cart, create a new cart item
                            if(!cartReady) {
                                cartSetup newItems = new cartSetup(selectedGame.getTitle(), selectedGame.getPrice());
                                newItems.setQuantity(copies);
                                userCart.add(newItems);
                        }
                            // Display message of the addition of games to cart
                            resultArea.setText("Added " + copies + " copies of " + selectedGame.getTitle() + " your cart");
                    }
                } catch (NumberFormatException e) {
                        //JOptionPane.showMessageDialog(null, "Please enter a valid number");
                        resultArea.append("Invalid input");
                    }
                } else {
                    resultArea.append("No games found");
                }
            }
            private void viewCart() {
                resultArea.setText("");
                // Check if the cart is empty
                if(userCart.isEmpty()) {
                    resultArea.append("No Games In cart found");
                    return;
                }


                // Build a string with details of each item in the cart
                StringBuilder cartContents = new StringBuilder("Your Cart:\n");
                for (cartSetup item : userCart) {
                    cartContents.append(item.getTitle()).append(" - $").append(item.getPrice())
                            .append(" x ").append(item.getQuantity()).append("\n");
                }
                // Display cart contents in the result area
                resultArea.setText(cartContents.toString());
            }

    private void checkoutFinal() {
        resultArea.setText("");
        if (userCart.isEmpty()) {
            resultArea.setText("No games in cart to checkout.");
            return;
        }

        double total = 0;
        StringBuilder checkoutDetails = new StringBuilder("Checkout:\n");
        String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";

        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            connection.setAutoCommit(false); // Start a transaction

            for (cartSetup item : userCart) {
                double itemTotal = item.getPrice() * item.getQuantity();
                checkoutDetails.append(item.getTitle()).append(" - $").append(item.getPrice())
                        .append(" x ").append(item.getQuantity()).append(" = $").append(itemTotal).append("\n");
                total += itemTotal;

                // Deduct the quantity from the stock in `add_games`
                /*String updateStockQuery = "UPDATE add_games SET stock = stock - ? WHERE title = ?";
                try (PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {
                    updateStockStmt.setInt(1, item.getQuantity());
                    updateStockStmt.setString(2, item.getTitle());
                    updateStockStmt.executeUpdate();
                }

                 */

                // Insert transaction record
                String insertTransactionQuery = "INSERT INTO transactions (gameId, title, quantity, price, total) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement insertTransactionStmt = connection.prepareStatement(insertTransactionQuery)) {
                    insertTransactionStmt.setInt(1, selectedGame.getGameid());
                    insertTransactionStmt.setString(2, item.getTitle());
                    insertTransactionStmt.setInt(3, item.getQuantity());
                    insertTransactionStmt.setDouble(4, item.getPrice());
                    insertTransactionStmt.setDouble(5, itemTotal);
                    insertTransactionStmt.executeUpdate();
                }
            }

            connection.commit(); // Commit the transaction if all updates were successful
            checkoutDetails.append("Total: $").append(total);

            int answer = JOptionPane.showConfirmDialog(buyGamesFrame, checkoutDetails.toString() + "\nConfirm Purchase?");
            if (answer == JOptionPane.YES_OPTION) {
                resultArea.setText("Thank you for your purchase!");
                userCart.clear();
            } else {
                connection.rollback(); // Roll back transaction if user cancels
                resultArea.setText("Checkout canceled.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            resultArea.setText("Error processing checkout: " + e.getMessage());
        }
    }
    // This is to exit this class
    private void exitToMainMenu() {
        buyGamesFrame.dispose();
        if (mainMenuRunnable != null) {
            mainMenuRunnable.run();
        }
    }
}




