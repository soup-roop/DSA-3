// File: src/ui/OverwatchUI.java
package ui;

import dsa.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class OverwatchUI extends JFrame {
    private User user;
    private GraphEngine engine;
    private JLabel lblGlobalTimer;
    private JTable table;
    private Timer uiTimer;

    public OverwatchUI(User user, GraphEngine engine) {
        this.user = user;
        this.engine = engine;
        initUI();
        startLiveTimer(); 
    }

    private void startLiveTimer() {
        uiTimer = new Timer(1000, e -> {
            long closest = Long.MAX_VALUE;
            boolean hasActive = false;
            
            for (Task t : engine.taskMap.values()) {
                if (!t.isCompleted && !t.timeMode.equals("NONE")) {
                    hasActive = true;
                    if (t.deadlineMs < closest) closest = t.deadlineMs;
                }
            }
            
            if (!hasActive) {
                lblGlobalTimer.setText("NO ACTIVE TIMERS");
                lblGlobalTimer.setForeground(new Color(100, 100, 100));
            } else {
                long diff = closest - System.currentTimeMillis();
                if (diff <= 0) {
                    lblGlobalTimer.setText("⚠ TIME UP!");
                    lblGlobalTimer.setForeground(new Color(255, 69, 0)); 
                } else {
                    long s = diff / 1000, d = s / 86400, h = (s % 86400) / 3600, m = (s % 3600) / 60, sec = s % 60;
                    if (d > 0) lblGlobalTimer.setText(String.format("%dd %02dh %02dm", d, h, m)); 
                    else lblGlobalTimer.setText(String.format("%02d:%02d:%02d", h, m, sec));
                    lblGlobalTimer.setForeground(new Color(0, 200, 255));
                }
            }
            if (table != null) table.repaint();
        });
        uiTimer.start();
    }

    private void initUI() {
        setTitle("OVERWATCH - Enterprise Analytics | " + user.username);
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(15, 15, 18));

        // --- TOP METRICS PANEL ---
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(new Color(15, 15, 18));
        topContainer.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel metricsPanel = new JPanel(new GridLayout(1, 5, 15, 15));
        metricsPanel.setBackground(new Color(15, 15, 18));
        
        int totalTasks = engine.taskMap.size();
        int completedTasks = (int) engine.taskMap.values().stream().filter(t -> t.isCompleted).count();
        int completionPercentage = totalTasks == 0 ? 0 : (completedTasks * 100) / totalTasks;

        lblGlobalTimer = new JLabel("CALCULATING...", SwingConstants.CENTER);
        lblGlobalTimer.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        
        JPanel timerCard = createMetricCard("NEXT DEADLINE", "", new Color(0, 200, 255));
        timerCard.add(lblGlobalTimer);

        metricsPanel.add(timerCard);
        metricsPanel.add(createMetricCard("TOTAL TASKS", String.valueOf(totalTasks), new Color(30, 144, 255)));
        metricsPanel.add(createMetricCard("COMPLETED", completedTasks + " (" + completionPercentage + "%)", new Color(50, 205, 50)));
        metricsPanel.add(createMetricCard("MIN DURATION", engine.totalProjectDuration + " Hours", new Color(186, 85, 211)));
        
        JButton btnLogout = createStyledButton("LOGOUT", new Color(40, 40, 45), Color.WHITE);
        btnLogout.addActionListener(e -> { 
            if(uiTimer != null) uiTimer.stop(); 
            new AuthWindow().setVisible(true); 
            dispose(); 
        });
        JPanel logoutPanel = new JPanel(new GridBagLayout());
        logoutPanel.setBackground(new Color(15, 15, 18));
        logoutPanel.add(btnLogout);
        metricsPanel.add(logoutPanel);

        topContainer.add(metricsPanel, BorderLayout.CENTER);

        // --- TABLE SETUP ---
        String[] cols = {"ID", "Task Name", "Time Left", "EST", "EFT", "Slack", "Critical", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (String id : engine.topologicalOrder) {
            Task t = engine.taskMap.get(id);
            String status = t.isCompleted ? "✅ DONE" : "PENDING";
            model.addRow(new Object[]{t.id, t.name, t, t.est, t.eft, t.slack, t.isCritical ? "⚠ YES" : "NO", status});
        }

        table = new JTable(model);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(45, 45, 55));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setBackground(new Color(22, 22, 26));
        table.getTableHeader().setForeground(new Color(150, 150, 150));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(40, 40, 45)));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object v, boolean isSel, boolean foc, int r, int c) {
                
                // CRITICAL PATH CUSTOM BADGE
                if (c == 6) {
                    String crit = v.toString();
                    return new JPanel(new GridBagLayout()) {
                        {
                            setOpaque(true);
                            setBackground(isSel ? new Color(45, 45, 55) : new Color(20, 20, 24));
                            if (crit.equals("⚠ YES")) {
                                JLabel badge = new JLabel("CRITICAL", SwingConstants.CENTER) {
                                    protected void paintComponent(Graphics g) {
                                        Graphics2D g2 = (Graphics2D) g.create();
                                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                        g2.setColor(new Color(180, 40, 40)); // Deep Red
                                        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                                        g2.dispose();
                                        super.paintComponent(g);
                                    }
                                };
                                badge.setForeground(Color.WHITE);
                                badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
                                badge.setBorder(new EmptyBorder(3, 10, 3, 10));
                                add(badge);
                            } else {
                                JLabel lbl = new JLabel("-");
                                lbl.setForeground(new Color(100, 100, 100));
                                add(lbl);
                            }
                        }
                    };
                }

                Component comp = super.getTableCellRendererComponent(tbl, v, isSel, foc, r, c);
                ((JComponent) comp).setBorder(new EmptyBorder(0, 15, 0, 15)); 
                comp.setBackground(isSel ? new Color(45, 45, 55) : new Color(20, 20, 24));
                
                if (c == 2 && v instanceof Task) {
                    Task t = (Task) v;
                    if (t.timeMode.equals("NONE")) {
                        setText("NO LIMIT");
                        comp.setForeground(new Color(100, 100, 100));
                    } else if (t.isCompleted) {
                        setText("--:--:--");
                        comp.setForeground(new Color(100, 100, 100));
                    } else {
                        long diff = t.deadlineMs - System.currentTimeMillis();
                        if (diff <= 0) {
                            setText("⚠ TIME UP");
                            comp.setForeground(new Color(255, 100, 100));
                        } else {
                            long secs = diff / 1000, d = secs / 86400, h = (secs % 86400) / 3600, m = (secs % 3600) / 60, sec = secs % 60;
                            if (d > 0) setText(String.format("%dd %02d:%02d:%02d", d, h, m, sec));
                            else setText(String.format("%02d:%02d:%02d", h, m, sec));
                            comp.setForeground(Color.WHITE);
                        }
                    }
                    return comp;
                }

                String status = (String) tbl.getModel().getValueAt(r, 7);
                if (c == 7) {
                    comp.setForeground(status.equals("✅ DONE") ? new Color(50, 205, 50) : new Color(150, 150, 150));
                } else {
                    comp.setForeground(status.equals("✅ DONE") ? new Color(100, 100, 100) : Color.WHITE);
                }
                return comp;
            }
        });

        // CUSTOM SCROLLBAR
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(20, 20, 24));
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() { this.thumbColor = new Color(70, 70, 80); this.trackColor = new Color(20, 20, 24); }
            @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() { JButton b = new JButton(); b.setPreferredSize(new Dimension(0, 0)); return b; }
        });

        // --- ACTION BAR ---
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        actionBar.setBackground(new Color(22, 22, 26));
        actionBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(40, 40, 45)));
        
        JLabel actionLbl = new JLabel("Target Task:");
        actionLbl.setForeground(Color.LIGHT_GRAY);
        actionBar.add(actionLbl);
        
        JComboBox<String> pendingTasks = new JComboBox<>();
        engine.topologicalOrder.stream().map(engine.taskMap::get)
            .filter(t -> !t.isCompleted)
            .forEach(t -> pendingTasks.addItem(t.id + " - " + t.name));
        actionBar.add(pendingTasks);

        JButton btnComplete = createStyledButton("MARK AS COMPLETE", new Color(0, 128, 128), Color.WHITE);
        btnComplete.addActionListener(e -> {
            if (pendingTasks.getSelectedItem() != null) {
                String selectedId = pendingTasks.getSelectedItem().toString().split(" - ")[0];
                Task t = engine.taskMap.get(selectedId);
                t.isCompleted = true;
                
                int xpToGive = t.expReward;
                if (!t.timeMode.equals("NONE") && System.currentTimeMillis() > t.deadlineMs) {
                    xpToGive = Math.max(1, xpToGive / 2);
                    JOptionPane.showMessageDialog(this, "Task finished late. Half XP awarded: +" + xpToGive + " XP", "Late Completion", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Task finished on time! Full XP awarded: +" + xpToGive + " XP", "Task Cleared", JOptionPane.INFORMATION_MESSAGE);
                }

                user.addXP(xpToGive); 
                UserManager.saveUsersToCSV(); 
                engine.saveActiveGraph(); 
                
                if(uiTimer != null) uiTimer.stop();
                getContentPane().removeAll();
                initUI();
                startLiveTimer();
                revalidate();
                repaint();
            }
        });
        actionBar.add(btnComplete);

        JButton btnForfeit = createStyledButton("PURGE PROJECT", new Color(139, 0, 0), Color.WHITE);
        btnForfeit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Purge current project entirely?", "Warning", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                engine.deleteActiveFile(); 
                if(uiTimer != null) uiTimer.stop();
                new ModeWindow(user).setVisible(true); 
                dispose();
            }
        });
        actionBar.add(btnForfeit);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionBar, BorderLayout.SOUTH);
    }

    // Modern Left-Accented Metric Card
    private JPanel createMetricCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new GridLayout(2, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(accentColor);
                g.fillRect(0, 0, 4, getHeight()); // Left side accent line
            }
        };
        card.setBackground(new Color(24, 24, 28));
        card.setBorder(new EmptyBorder(10, 15, 10, 10));
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(new Color(140, 140, 150));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(Color.WHITE);
        
        card.add(lblTitle);
        if (!value.isEmpty()) card.add(lblValue);
        return card;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}