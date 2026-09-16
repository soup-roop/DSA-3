// File: src/ui/TaskInputUI.java
package ui;

import dsa.GraphEngine;
import dsa.Task;
import dsa.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TaskInputUI extends JFrame {
    private GraphEngine engine;
    private DefaultTableModel tableModel;

    public TaskInputUI(User user, String mode) {
        engine = new GraphEngine();
        
        setTitle("Project Builder - " + mode);
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(18, 18, 18));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(25, 25, 25));
        formPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)), "Add New Task"));
        ((javax.swing.border.TitledBorder)formPanel.getBorder()).setTitleColor(Color.LIGHT_GRAY);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtId = new JTextField(12);
        JTextField txtName = new JTextField(12);
        JTextField txtDuration = new JTextField(5);
        JComboBox<String> cmbUnit = new JComboBox<>(new String[]{"Hours", "Days"});
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        timePanel.setBackground(new Color(25, 25, 25));
        timePanel.add(txtDuration);
        timePanel.add(cmbUnit);
        JTextField txtDeps = new JTextField(12);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; formPanel.add(new JLabel("Task ID:"), gbc);
        gbc.gridx = 1; formPanel.add(txtId, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(new JLabel("Task Name:"), gbc);
        gbc.gridx = 1; formPanel.add(txtName, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(new JLabel("Time Estimate:"), gbc);
        gbc.gridx = 1; formPanel.add(timePanel, gbc);
        
        gbc.gridx = 0; gbc.gridy = ++row; formPanel.add(new JLabel("Prerequisites:"), gbc);
        gbc.gridx = 1; formPanel.add(txtDeps, gbc);

        JButton btnAdd = new JButton("Add Task to Graph");
        btnAdd.setBackground(new Color(0, 120, 215));
        btnAdd.addActionListener(e -> {
            try {
                String id = txtId.getText().trim();
                String name = txtName.getText().trim();
                int timeVal = Integer.parseInt(txtDuration.getText().trim());
                List<String> deps = txtDeps.getText().trim().isEmpty() ? 
                    List.of() : Arrays.stream(txtDeps.getText().split(",")).map(String::trim).collect(Collectors.toList());

                if (id.isEmpty() || name.isEmpty()) throw new IllegalArgumentException("ID and Name cannot be empty.");
                if (engine.taskMap.containsKey(id)) throw new IllegalArgumentException("Task ID already exists!");

                int durationInHours = cmbUnit.getSelectedItem().equals("Days") ? timeVal * 24 : timeVal;
                
                Task t = new Task(id, name, durationInHours, null, deps);
                t.timeMode = "TIMER"; 
                t.deadlineMs = System.currentTimeMillis() + (durationInHours * 3600000L); 
                
                engine.addTask(t);
                tableModel.addRow(new Object[]{id, name, deps.isEmpty() ? "None" : String.join(", ", deps), durationInHours + " Hours"});
                
                txtId.setText(""); txtName.setText(""); txtDuration.setText(""); txtDeps.setText("");
                txtId.requestFocus();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0; gbc.gridy = ++row; gbc.gridwidth = 2;
        formPanel.add(btnAdd, gbc);

        JPanel topActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topActionPanel.setBackground(new Color(18, 18, 18));
        JButton btnImport = new JButton("📂 IMPORT CSV");
        btnImport.setBackground(new Color(50, 50, 50));
        btnImport.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (engine.loadCustomCSV(chooser.getSelectedFile())) {
                    tableModel.setRowCount(0); 
                    for (Task t : engine.taskMap.values()) {
                        String d = t.dependencies.isEmpty() ? "None" : String.join(", ", t.dependencies);
                        tableModel.addRow(new Object[]{t.id, t.name, d, t.duration + " Hours"});
                    }
                    JOptionPane.showMessageDialog(this, "Loaded " + engine.taskMap.size() + " tasks successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Import failed. See the error popups for details.", "Import Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        topActionPanel.add(btnImport);

        String[] cols = {"ID", "Name", "Dependencies", "Total Duration"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.add(topActionPanel, BorderLayout.NORTH);
        centerContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton btnLaunch = new JButton("LAUNCH " + mode + " DASHBOARD");
        btnLaunch.setBackground(new Color(50, 205, 50));
        btnLaunch.setPreferredSize(new Dimension(100, 50));
        btnLaunch.addActionListener(e -> {
            if (engine.taskMap.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one task!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                engine.buildGraph();
                if (engine.processProject()) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                    chooser.setSelectedFile(new File("my_project.csv"));
                    if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        engine.activeFile = chooser.getSelectedFile();
                        engine.saveActiveGraph(); 
                        
                        if (mode.equals("OVERWATCH")) new OverwatchUI(user, engine).setVisible(true);
                        else new OverdriveUI(user, engine).setVisible(true);
                        dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "DEADLOCK DETECTED! Check your prerequisites.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(formPanel, BorderLayout.WEST);
        add(centerContainer, BorderLayout.CENTER);
        add(btnLaunch, BorderLayout.SOUTH);
    }
}