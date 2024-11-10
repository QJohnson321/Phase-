import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

// This class allows users to add games. The user can
// add games by typing in the attributes or they can
// add it with a txt file

public class addGameGui {
    Scanner scanner = new Scanner(System.in);
    private JFrame addGamesFrame;
    private JTextField gameIdTxt, titleTxt, genreTxt, studioTxt, consoleTxt, multiplayerTxt, productTypeTxt, releaseDMYearTxt, priceTxt;
    //int gameId;
    //double price;
    String regex = "\\d{1,2}-\\d{1,2}-\\d{4}";
    private JComboBox <String> consoleJbox, multiplayerJbox;
    private Runnable mainMenuRunnable;

    List<gameAttributes> gamesStored;

    public addGameGui(List<gameAttributes> gamesStored, Runnable mainMenuRunnable) {
        this.gamesStored = gamesStored;
        this.mainMenuRunnable = mainMenuRunnable;
        //addGames();
    }

    // This is the Gui of the add games class
    // I am setting up the basics of the gui
    // such as the size, exiting the gui when i click the X button

    public void addGames() {
        addGamesFrame = new JFrame("Add Games");
        addGamesFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //addGamesFrame.setTitle("Adding Game");
        addGamesFrame.setSize(800, 600);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6,2,10,10));

        //I have created text fields for the user to enter their attributes
        gameIdTxt = new JTextField();
        titleTxt = new JTextField();
        genreTxt = new JTextField();
        studioTxt = new JTextField();
        consoleTxt = new JTextField();
        multiplayerTxt = new JTextField();
        productTypeTxt = new JTextField();
        releaseDMYearTxt = new JTextField();
        priceTxt = new JTextField();

        // Here I add the labels and the text fields to the frame
        panel.add(new JLabel("Enter game id"));
        gameIdTxt = new JTextField();
        panel.add(gameIdTxt);

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
        // type in the name of the console. I do give the user
        // the option to type in a console if I do not have it as an option
        panel.add(new JLabel("Enter Console"));
        String[] consoleOptions = {" ", "Playstation", "Xbox", "Nintendo", "Sega", "Other" };
        consoleJbox = new JComboBox<>(consoleOptions);
        panel.add(consoleJbox);


        panel.add(new JLabel("Enter Release Date ##-##-####"));


        releaseDMYearTxt = new JTextField();
        panel.add(releaseDMYearTxt);

        // I used a Jbox for this so the user doesn't have to type
        // in if the game has multiplayer or not
        panel.add(new JLabel("Does this game have Multiplayer"));
        String[] multiplayerOption = {" ", "Yes", "No"};
        multiplayerJbox = new JComboBox<>(multiplayerOption);
        panel.add(multiplayerJbox);

        panel.add(new JLabel("Enter Product Type"));
        productTypeTxt = new JTextField();
        panel.add(productTypeTxt);

        panel.add(new JLabel("Enter Price"));
        priceTxt = new JTextField();
        panel.add(priceTxt);

        // These are the action listener for the buttons
        JButton confirmAddGameButton = new JButton("Add This Game");
        panel.add(confirmAddGameButton);
        confirmAddGameButton.addActionListener(e -> addGamesToDb());
        //panel.add(confirmAddGameButton);

        // this is the exit button
        JButton exitButton = new JButton("Exit");
        panel.add(exitButton);
        exitButton.addActionListener(e -> exitHere());

        // This gives allows the user to upload games via
        // txt file
        JButton batchingBtn = new JButton("Upload multiple games");
        batchingBtn.addActionListener(e -> batchUpload());
        panel.add(batchingBtn);

        /*
        confirmAddGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addGamesToDb();
            }
        });

         */

        addGamesFrame.add(panel);
        addGamesFrame.setLocationRelativeTo(null);
        addGamesFrame.setVisible(true);
    }


    // This is the logic to add games to the database


            //This is to make sure that the user can only enter an int
// That's why the gameID is set at -1 once the user enters
// a positive number greater than 0 the program will keep that number
// If the user enters anything other than an int the Integer.parseInt(gameId1);
// will trigger the (Exception e)
// I have added an if else statement to check the game id and if the
// game id already exist then a error message will pop up
// if the game id is not found in the system then we can add it
            private void addGamesToDb() {
                try {
                    int newGameId = Integer.parseInt(gameIdTxt.getText());
                    boolean gameFound = gamesStored.stream().anyMatch(game -> game.getGameid() == newGameId);
                    if (gameFound) {
                        JOptionPane.showMessageDialog(addGamesFrame, "Game ID already exists. Enter a different Id");
                        return;
                    }

// this is used to get the text from the game that was found
                    //int gameId = Integer.parseInt(gameIdTxt.getText());
                    String title = titleTxt.getText();
                    String genre = genreTxt.getText();
                    String studio = studioTxt.getText();
                    String releaseDMYear = releaseDMYearTxt.getText();
                    //String console = consoleTxt.getText();
                    //double price = Double.parseDouble(priceTxt.getText());
                    double price = 0.00;


                    //Console Choice
                    String console1 = (String) consoleJbox.getSelectedItem();
                    if (console1.equals("Other")) {
                        console1 = JOptionPane.showInputDialog(addGamesFrame, "Enter Console");
                    }

                    String multiplayer = (String) multiplayerJbox.getSelectedItem();
                    String productType = productTypeTxt.getText();

                    // This checks if the date is entered in correctly
                    if (!Pattern.matches(regex, releaseDMYear)) {
                        JOptionPane.showMessageDialog(addGamesFrame, "Invalid Release Date");
                        return;
                    }

                    if (studio.isEmpty() || productType.isEmpty()) {
                        JOptionPane.showMessageDialog(addGamesFrame, "Fill in these fields");
                        return;
                    }

                    // This is to check if the game has a price that is greater than 0.
                    // The number is accepted when it is aboce 0 and denied when its equal to or
                    // below 0
                    try {
                        price = Double.parseDouble(priceTxt.getText());
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(addGamesFrame, "Price must be greater than 0");
                            return;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(addGamesFrame, "Price must be a number");
                        return;
                    }

                    //Name of the file
                    String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";
                    //String dbUser = "root";
                    //String dbPass = "SmoothBeats@321";

                    String insertQuery = "INSERT INTO add_games(gameId, title, genre, release_date, console, studio, product_type,multiplayer,price) " +
                            "VALUES (?,?,?,?,?,?,?,?,?)";

                    String checkUp = "SELECT COUNT(*) FROM add_games WHERE gameId =?";
                    try (Connection connection = DriverManager.getConnection(dbUrl);

                         PreparedStatement checkState = connection.prepareStatement(checkUp)) {
                        checkState.setInt(1, newGameId);
                        ResultSet rs = checkState.executeQuery();
                        if (rs.next() && rs.getInt(1) > 0) {
                            JOptionPane.showMessageDialog(addGamesFrame, "Game already exists");
                            return;
                        }
                        try (PreparedStatement state = connection.prepareStatement(insertQuery)) {


                            state.setInt(1, newGameId);
                            state.setString(2, title);
                            state.setString(3, genre);
                            state.setString(4, releaseDMYear);
                            state.setString(5, console1);
                            state.setString(6, studio);
                            state.setString(7, productType);
                            state.setString(8, multiplayer);
                            state.setDouble(9, price);

                            int rowInserted = state.executeUpdate();
                            if (rowInserted > 0) {
                                JOptionPane.showMessageDialog(addGamesFrame, "Game added successfully");
                            }


                            // Once everything is collectd then we are going to store the data in addNewGame
                            //then the game will be added to gamesStored
                            gameAttributes addNewGame = new gameAttributes(newGameId, title, genre, releaseDMYear, console1, studio, productType, multiplayer, price);
                            gamesStored.add(addNewGame);

                            // This is just a pop up message to let the user know that the game has been added
                            // Once a game is added then the system will ask the user if they would like to
                            // add another game or not. If they select yes the text fields will clear
                            int lastChoice = JOptionPane.showOptionDialog(addGamesFrame, "Game Added",
                                    "Would you like to add another game or return To main menu?",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                    new String[]{"Add Another Game", "Main Menu"}, "Main Menu");

                            if (lastChoice == JOptionPane.YES_OPTION) {
                                gameIdTxt.setText("");
                                titleTxt.setText("");
                                genreTxt.setText("");
                                studioTxt.setText("");
                                consoleJbox.setSelectedIndex(0);
                                multiplayerJbox.setSelectedIndex(0);
                                productTypeTxt.setText("");
                                releaseDMYearTxt.setText("");
                                priceTxt.setText("");

                                // If they select no then the user will return to the main menu
                            } else {
                                addGamesFrame.dispose();
                                mainMenuRunnable.run();
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(addGamesFrame, ex.getMessage());
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(addGamesFrame, "Enter a valid ID");
                        return;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                } finally {

                }
            }


    // This is the logic that allows the user to upload a text file to
    // the database
    private void batchUpload(){

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // This will allow the user to only upload a tcxt file to the database
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text File", "txt"));
        int returnVal = chooser.showOpenDialog(addGamesFrame);

        // This allows the user to choose a file
        // once chosen then the program will read the file
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            readFile(selectedFile);
        }
    }

    // This is to format the release date
    private static final String DATE_REGEX = "\\d{1,2} - \\d{1,2} - \\d{4}";

    // This opens the file and reads it line by line
    private void readFile(File file) {

        String dbUrl = "jdbc:sqlite:./SQL DataBase/mydbb.db";
        //String dbUser = "root";
        //String dbPass = "SmoothBeats@321";

        String insertQuery = "INSERT INTO add_games (gameId, title, genre, release_date, console, studio, product_type, multiplayer, price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String checkQuery = "SELECT COUNT(*) FROM add_games WHERE gameId = ?";


        try (Connection connection = DriverManager.getConnection(dbUrl);
             BufferedReader reader = new BufferedReader(new FileReader(file))) {


            String line;
            while((line = reader.readLine()) != null) {

                //This removes any parentheses, quotes, and semicolons and trims any whitespace
                line = line.replaceAll("[()\";]", "").trim();

                // Split by commas ignore whitespaces
                String[] data = line.split(",\\s*" );

                // this checks if the attributes contains 9 attributes if not

                if (data.length != 9) {
                    //System.out.println("Invalid format" );
                    continue;
                }

                try {
                    int gameId = Integer.parseInt(data[0]);
                    String title = data[1].trim();
                    String genre = data[2].trim();
                    String releaseDMYear = data[3].trim();
                    String console = data[4].trim();
                    String studio = data[5].trim();
                    String productType = data[6].trim();
                    String multiplayer = data[7].trim();
                    //String releaseDMYear = data[7].trim();
                    double price = Double.parseDouble(data[8].trim());

                    try(PreparedStatement stateCheck = connection.prepareStatement(checkQuery)) {
                        stateCheck.setInt(1, gameId);
                        ResultSet rs = stateCheck.executeQuery();
                            if (rs.next() && rs.getInt(1)>0) {
                                System.out.println("Duplicate Game" + gameId);
                                continue;
                            }
                    }

                    // Check the game id has a duplicate
                    boolean gameFound = gamesStored.stream().anyMatch(game -> game.getGameid() == gameId);
                    if (gameFound) {
                        System.out.println("Duplicate Game ID" + gameId);
                        continue;
                    }



                    // checks date format and ensure price is over 0
                    if(Pattern.matches(DATE_REGEX, releaseDMYear) && price> 0) {

                        try(PreparedStatement state = connection.prepareStatement(insertQuery)) {
                            state.setInt(1, gameId);
                            state.setString(2, title);
                            state.setString(3, genre);
                            state.setString(4, releaseDMYear);
                            state.setString(5, console);
                            state.setString(6, studio);
                            state.setString(7, productType);
                            state.setString(8, multiplayer);
                            state.setDouble(9, price);

                            state.executeUpdate();

                            System.out.println(" Insert Game Id: " + gameId);
                        }

                        gameAttributes aNewGame = new gameAttributes(gameId, title, genre, releaseDMYear, console, studio, productType, multiplayer, price);
                        gamesStored.add(aNewGame);
                    } else {
                        // Print an error message if date or price are invalid
                        System.out.println("Invalid File " + line);
                    }
                } catch (NumberFormatException| SQLException e) {
                    System.out.println("Error With File" + line + "-" + e.getMessage() );
                }

            }
            // Informs user that all the games were added
            JOptionPane.showMessageDialog(addGamesFrame, "Your Games have been added to the database");
        } catch (IOException e) {
            // Informs user that all the games were not added
            JOptionPane.showMessageDialog(addGamesFrame, "Error reading file" + e.getMessage());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    // This is to exit this class
        private void exitHere() {
            addGamesFrame.dispose();
            if (mainMenuRunnable != null) {
                mainMenuRunnable.run();
            }
        }

        /*
        private void addTheseGames(){
            try {
                int newGameId = Integer.parseInt(gameIdTxt.getText());
                boolean gameFound = gamesStored.stream().anyMatch(game -> game.getGameid() == newGameId);
                if (gameFound) {
                    JOptionPane.showMessageDialog(addGamesFrame, "Game ID already exists. Enter a different Id");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addGamesFrame, "Enter a valid ID");
                return;
            }
               int gameId = Integer.parseInt(gameIdTxt.getText());
                String title = titleTxt.getText();
                String genre = genreTxt.getText();
                String releaseDMYear = releaseDMYearTxt.getText();
                String console = consoleTxt.getText();
                String studio = studioTxt.getText();
                String productType = productTypeTxt.getText();
                String multiplayer = multiplayerTxt.getText();
                double price = Double.parseDouble(priceTxt.getText());

                if(!Pattern.matches(DATE_REGEX, releaseDMYear)) {
                    JOptionPane.showMessageDialog(addGamesFrame, "Invalid Date Format");
                    return;
                }
                String dbUrl = "jdbc:mysql://127.0.0.1:3306/phase4_schema";
                String dbUser ="root";
                String dbPassword = "SmoothBeats@321";

                String insertQuery = "INSERT INTO add_games(idadd_games, title, genre, release_date, console, studio, product_type, multiplayer, price)" +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                                            PreparedStatement statement =connection.prepareStatement(insertQuery)) {

                    statement.setInt(1, gameId);
                    statement.setString(2, title);
                    statement.setString(3, genre);
                    statement.setString(4, releaseDMYear);
                    statement.setString(5, console);
                    statement.setString(6, studio);
                    statement.setString(7, productType);
                    statement.setString(8, multiplayer);
                    statement.setDouble(9, price);

                    int rowsInserted = statement.executeUpdate();
                        if (rowsInserted > 0) {
                            JOptionPane.showMessageDialog(addGamesFrame, "Game Added");
                        }
                        gameAttributes addGames = new gameAttributes(gameId, title, genre, releaseDMYear, console, studio, productType, multiplayer, price);
                        gamesStored.add(addGames);

                        int lastChoice = JOptionPane.showOptionDialog(addGamesFrame,"Games Added", "Would yoi like to add another game or return to main menu?",
                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Add Another Game", "Main Menu"}, "Main Menu");

                        if (lastChoice == JOptionPane.YES_OPTION) {
                            gameIdTxt.setText("");
                            titleTxt.setText("");
                            genreTxt.setText("");
                            releaseDMYearTxt.setText("");
                            consoleJbox.setSelectedIndex(0);
                            studioTxt.setText("");
                            productTypeTxt.setText("");
                            multiplayerJbox.setSelectedIndex(0);
                            priceTxt.setText("");
                        } else {
                            addGamesFrame.dispose();
                            mainMenuRunnable.run();
                        }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

        }

         */
    }


