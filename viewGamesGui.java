import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class viewGamesGui {

    private Runnable mainMenuRunnable;
    List<gameAttributes> gamesStored;
    private JFrame viewGamesFrame;
    private JTextField titleTxt, genreTxt, studioTxt, consoleTxt, exit;
    private JTextArea resultArea;


    public viewGamesGui(List<gameAttributes> gamesStored, Runnable mainMenuRunnable) {
        this.gamesStored = gamesStored;
        this.mainMenuRunnable = mainMenuRunnable;
        //buildViewGui();
    }
        // This is the Gui of the view games class
        // I am setting up the basics of the gui
        // such as the size, exiting the gui when i click the X button

        public void buildViewGui() {
            viewGamesFrame = new JFrame("View Games");
            viewGamesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //addGamesFrame.setTitle("Adding Game");
            viewGamesFrame.setSize(800, 600);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(6,2,10,10));

            //I have created text fields for the user to enter their attributes
            titleTxt = new JTextField();
            genreTxt = new JTextField();
            studioTxt = new JTextField();
            consoleTxt = new JTextField();
            exit = new JTextField();


            // Here I add the labels and the text fields to the frame
            panel.add(new JLabel("Enter Title"));
            titleTxt = new JTextField();
            panel.add(titleTxt);

            panel.add(new JLabel("Enter Genre"));
            genreTxt = new JTextField();
            panel.add(genreTxt);


            panel.add(new JLabel("Enter Studio"));
            studioTxt = new JTextField();
            panel.add(studioTxt);

            // I used a Jbox on this so the user doesn't have to
            // type in the name of the console.
            panel.add(new JLabel("Enter Console"));
            //String[] consoleOptions = {" ", "Playstation", "Xbox", "Nintendo", "Sega", "Other" };
            consoleTxt = new JTextField();
            panel.add(consoleTxt);

            panel.add(new JLabel("Exit"));
            //consoleTxt = new JTextField();
            //panel.add(consoleTxt);

            // This is the action listener for the button
            JButton searchGamesButton = new JButton("View Games");
            searchGamesButton.addActionListener(e -> theFilters());
            panel.add(searchGamesButton);

            // This is the action listener for the exit button
            JButton exitButton = new JButton("Exit");
            exitButton.addActionListener(e -> exitCall());
            panel.add(exitButton);



            resultArea = new JTextArea(); // Making a text area to display results
            resultArea.setEditable(false); // Makes the text area non-editable
            resultArea.setLineWrap(true); // Wraps text lines that are too long
            resultArea.setWrapStyleWord(true) ;// Wraps lines at word boundaries


            viewGamesFrame.add(panel, BorderLayout.NORTH);
            viewGamesFrame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
            viewGamesFrame.setLocationRelativeTo(null);
            viewGamesFrame.setVisible(true);
        }

        private void theFilters() {

                // Gets user input from text fields, it changes it to lowercase and makes the information case-insensitive
            String title = titleTxt.getText().toLowerCase().trim();
            String genre = genreTxt.getText().toLowerCase().trim();
            String studio = studioTxt.getText().toLowerCase().trim();
            String console = consoleTxt.getText().toLowerCase().trim();


            // Filters gamesStored based on user input; empty fields are ignored
            List<gameAttributes> filteredList = gamesStored.stream()
                .filter(game -> title.isEmpty() || game.getTitle().toLowerCase().contains(title))
                    .filter(game -> genre.isEmpty() || game.getGenre().toLowerCase().contains(genre))
                    .filter(game -> studio.isEmpty() || game.getStudio().toLowerCase().contains(studio))
                    .filter(game -> console.isEmpty() || game.getConsole().toLowerCase().contains(console))

                    .collect(Collectors.toList()); //Collects results



            // calls the filtered method for user inputs
            try {
                filteredGames(title,genre,studio,console);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void filteredGames(String title, String genre, String studio, String console) throws SQLException {
            String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";
            //String dbUser = "root";
            //String dbPass = "SmoothBeats@321";

            //String selectQuery = "SELECT gameId, title, genre, release_date, console, studio, product_type,multiplayer, price FROM add_games";
            StringBuilder selectFilter = new StringBuilder("SELECT gameId, title, genre, release_date, console, studio, product_type, multiplayer, price FROM add_games WHERE 1=1 ");

                if(!title.isEmpty()) {
                    selectFilter.append("AND LOWER(title) LIKE ? ");
                }
                if(!genre.isEmpty()) {
                selectFilter.append("AND LOWER(genre) LIKE ? ");
                }
                if(!studio.isEmpty()) {
                    selectFilter.append("AND LOWER(studio) LIKE ? ");
                }
                if(!console.isEmpty()) {
                    selectFilter.append("AND LOWER(console) LIKE ? ");
                }

                String query = selectFilter.toString();

                    resultArea.setText(""); // Clears the previous results

                    try(Connection connection = DriverManager.getConnection(dbUrl);
                        PreparedStatement state = connection.prepareStatement(query)){

                        int startValue = 1;

                        if(!title.isEmpty()) {
                           state.setString(startValue++, "%" + title + "%");
                        }
                        if(!genre.isEmpty()) {
                            state.setString(startValue++, "%" + genre + "%");
                        }
                        if(!studio.isEmpty()) {
                            state.setString(startValue++, "%" + studio + "%");
                        }
                        if(!console.isEmpty()) {
                            state.setString(startValue++, "%" + console + "%");
                        }


            try(ResultSet resultSet = state.executeQuery()) {
                //PreparedStatement state = connection.prepareStatement(query);
                //ResultSet resultSet = state.executeQuery())


                if (!resultSet.isBeforeFirst()) {
                    resultArea.append("No Games Found");
                } else {
                    // Loops through each result
                    while (resultSet.next()) {
                        int gameId = resultSet.getInt("gameId");
                        String showTitle = resultSet.getString("title");
                        String showGenre = resultSet.getString("genre");
                        String showReleaseDate = resultSet.getString("release_date");
                        String showConsole = resultSet.getString("console");
                        String showStudio = resultSet.getString("studio");
                        String showProductType = resultSet.getString("product_type");
                        String showMultiplayer = resultSet.getString("multiplayer");
                        String showPrice = resultSet.getString("price");

                        // These are the displayed the results
                        resultArea.append("Game Id: " + gameId + "\n");
                        resultArea.append("Title: " + showTitle + "\n");
                        resultArea.append("Genre: " + showGenre + "\n");
                        resultArea.append("ReleaseDate: " + showReleaseDate + "\n");
                        resultArea.append("Studio: " + showStudio + "\n");
                        resultArea.append("Console: " + showConsole + "\n");
                        resultArea.append("Product: " + showProductType + "\n");
                        resultArea.append("Multiplayer: " + showMultiplayer + "\n");
                        resultArea.append("Price: " + showPrice + "\n");
                        resultArea.append("---------------------------------------\n\n"); // Separator line
                        //resultArea.append("\n");
                        // Loops through each game in the filtered list and displays its details in resultArea

                    }
                }
            }

            } catch (SQLException e) {
                e.printStackTrace();
                resultArea.setText("Something went wrong with the filtering ");
            }

        }
    // This is to exit this class
        public void exitCall() {
        viewGamesFrame.dispose();
            if (mainMenuRunnable != null) {
                mainMenuRunnable.run();
            }
        }

}
