package com.example.tonythomas__comp228__lab___5_____;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Exercise1 extends JFrame {
    // Database connection details//
    private static final String DB_URL = "jdbc:oracle:thin:@199.212.26.208:1521:SQLD";
    private static final String DB_USER = "COMP228_F24_soh_25";
    private static final String DB_PASSWORD = "password";

    // UI components//
    private JFrame frame;
    private JTextField playerIdField, firstNameField, lastNameField, addressField, postalCodeField, provinceField, phoneNumberField, gameIdField, gameTitleField, scoreField;
    private JTable table;
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        // Launch the application//
        SwingUtilities.invokeLater(() -> new Exercise1().createAndShowGUI());
    }

    public void createAndShowGUI() {
        // Initialize main frame//
        frame = new JFrame("Game Player GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new GridBagLayout());

        // Panel for input fields//
        JPanel inputPanel = new JPanel(new GridLayout(12, 2));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Player and Game Information"));

        // Initialize input fields for player and game details//
        playerIdField = new JTextField();
        firstNameField = new JTextField();
        lastNameField = new JTextField();
        addressField = new JTextField();
        postalCodeField = new JTextField();
        provinceField = new JTextField();
        phoneNumberField = new JTextField();
        gameIdField = new JTextField();
        gameTitleField = new JTextField();
        scoreField = new JTextField();

        // Add input fields to the input panel with labels//
        inputPanel.add(new JLabel("Player ID:"));
        inputPanel.add(playerIdField);
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);
        inputPanel.add(new JLabel("Address:"));
        inputPanel.add(addressField);
        inputPanel.add(new JLabel("Postal Code:"));
        inputPanel.add(postalCodeField);
        inputPanel.add(new JLabel("Province:"));
        inputPanel.add(provinceField);
        inputPanel.add(new JLabel("Phone Number:"));
        inputPanel.add(phoneNumberField);
        inputPanel.add(new JLabel("Game ID:"));
        inputPanel.add(gameIdField);
        inputPanel.add(new JLabel("Game Title:"));
        inputPanel.add(gameTitleField);
        inputPanel.add(new JLabel("Score:"));
        inputPanel.add(scoreField);

        // Panel for buttons//
        JPanel buttonPanel = new JPanel();
        JButton insertButton = new JButton("Insert");
        JButton updateButton = new JButton("Update");
        JButton displayButton = new JButton("Display");
        JButton clearButton = new JButton("Clear");
        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(displayButton);
        buttonPanel.add(clearButton);

        // Table to display player and game data//
        tableModel = new DefaultTableModel(new String[]{"Player ID", "First Name", "Last Name", "Game Title", "Score"}, 0);
        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Add components to the frame using GridBagLayout//
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0.4;
        gbc.insets = new Insets(5, 5, 5, 5);
        frame.add(inputPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0;
        frame.add(buttonPanel, gbc);

        gbc.gridy = 2;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        frame.add(tableScrollPane, gbc);

        // Add event listeners for buttons//
        insertButton.addActionListener(new InsertButtonListener());
        updateButton.addActionListener(new UpdateButtonListener());
        displayButton.addActionListener(e -> loadTableData());
        clearButton.addActionListener(e -> clearFields());

        // Handle table row selection to populate input fields//
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    playerIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    firstNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    lastNameField.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    gameTitleField.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    scoreField.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        // Final setup for frame//
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);

        // Load data into the table//
        loadTableData();
    }

    // Load data from database into JTable//
    private void loadTableData() {
        String query = "SELECT p.player_id, p.first_name, p.last_name, g.game_title, pg.score " +
                "FROM Player p " +
                "JOIN PlayerAndGame pg ON p.player_id = pg.player_id " +
                "JOIN Game g ON pg.game_id = g.game_id";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0); // Clear existing data in the table//
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("player_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("game_title"),
                        rs.getInt("score")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Clear all input fields//
    private void clearFields() {
        playerIdField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        postalCodeField.setText("");
        provinceField.setText("");
        phoneNumberField.setText("");
        gameIdField.setText("");
        gameTitleField.setText("");
        scoreField.setText("");
    }

    // Handle insertion of player and game data//
    private class InsertButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || gameTitleField.getText().isEmpty() || scoreField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all required fields.");
                return;
            }
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String insertPlayerSQL = "INSERT INTO Player (player_id, first_name, last_name, address, postal_code, province, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
                String insertGameSQL = "MERGE INTO Game USING DUAL ON (game_id = ?) WHEN NOT MATCHED THEN INSERT (game_id, game_title) VALUES (?, ?)";
                String insertPlayerGameSQL = "INSERT INTO PlayerAndGame (player_game_id, game_id, player_id, playing_date, score) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement playerStmt = conn.prepareStatement(insertPlayerSQL);
                     PreparedStatement gameStmt = conn.prepareStatement(insertGameSQL);
                     PreparedStatement playerGameStmt = conn.prepareStatement(insertPlayerGameSQL)) {

                    // Insert data into Player table//
                    playerStmt.setInt(1, Integer.parseInt(playerIdField.getText()));
                    playerStmt.setString(2, firstNameField.getText());
                    playerStmt.setString(3, lastNameField.getText());
                    playerStmt.setString(4, addressField.getText());
                    playerStmt.setString(5, postalCodeField.getText());
                    playerStmt.setString(6, provinceField.getText());
                    playerStmt.setString(7, phoneNumberField.getText());
                    playerStmt.executeUpdate();

                    // Insert data into Game table (if not exists)//
                    gameStmt.setInt(1, Integer.parseInt(gameIdField.getText()));
                    gameStmt.setInt(2, Integer.parseInt(gameIdField.getText()));
                    gameStmt.setString(3, gameTitleField.getText());
                    gameStmt.executeUpdate();

                    // Insert data into PlayerAndGame table//
                    playerGameStmt.setInt(1, Integer.parseInt(playerIdField.getText()) + Integer.parseInt(gameIdField.getText())); // Example composite key
                    playerGameStmt.setInt(2, Integer.parseInt(gameIdField.getText()));
                    playerGameStmt.setInt(3, Integer.parseInt(playerIdField.getText()));
                    playerGameStmt.setDate(4, new java.sql.Date(System.currentTimeMillis())); // Using current date as playing date
                    playerGameStmt.setInt(5, Integer.parseInt(scoreField.getText()));
                    playerGameStmt.executeUpdate();
                }

                JOptionPane.showMessageDialog(frame, "Data inserted successfully.");
                loadTableData();
                clearFields();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error inserting data: " + ex.getMessage());
            }
        }
    }

    // Handle update of existing player and game data//
    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Implementation for updating player and game data//
            // Similar structure to insert but using SQL UPDATE statements//
        }
    }
}
//program ends//