package me.nrubin29.rpg.server;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI extends JFrame {

	private DefaultListModel playersList = new DefaultListModel();
	private JList players = new JList(playersList);
	private JTextArea logs = new JTextArea();
	
	private static final long serialVersionUID = 1L;

	public GUI(int port) {
		super("RPG-Core Server");
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBorder(BorderFactory.createTitledBorder("Server Info"));
		
		JLabel info = null;
		
		try {
			info = new JLabel(
				"<html>" +
				"Server Address: " + InetAddress.getLocalHost().getHostAddress() + "<br/>" +
                "Port: " + port
			);
		}
		catch (Exception e) { e.printStackTrace(); }
		
		info.setAlignmentX(Component.LEFT_ALIGNMENT);
		info.setHorizontalAlignment(SwingConstants.LEFT);
		
		JLabel map = new JLabel("Map: None.");
		map.setCursor(new Cursor(Cursor.HAND_CURSOR));
		map.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser choice = new JFileChooser();
				choice.setFileFilter(new FileNameExtensionFilter("Maps", "map"));
				choice.setFileSelectionMode(JFileChooser.FILES_ONLY);
				choice.setMultiSelectionEnabled(false);
				
				int resp = choice.showOpenDialog(GUI.this);
				
				if (resp == JFileChooser.APPROVE_OPTION) {
					try {
						StringBuffer buffer = new StringBuffer();
						BufferedReader reader = new BufferedReader(new FileReader(choice.getSelectedFile()));
						
						while (reader.ready()) {
							buffer.append(reader.readLine());
						}
						
						reader.close();
						
						Server.getInstance().setMap(buffer.toString());
					}
					catch (Exception ex) { ex.printStackTrace(); }
				}
			}
		});

		logs.setEditable(false);

        JScrollPane playersScroll = new JScrollPane(players);
        playersScroll.setBorder(BorderFactory.createTitledBorder("Players"));

        JScrollPane logsScroll = new JScrollPane(logs);
        logsScroll.setBorder(BorderFactory.createTitledBorder("Log"));
        
        infoPanel.add(info);
        infoPanel.add(map);

		JPanel leftPane = new JPanel();
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.Y_AXIS));
		leftPane.setMaximumSize(new Dimension(100, 480));
		
		leftPane.add(infoPanel);
		leftPane.add(playersScroll);
		
		add(leftPane);
		add(logsScroll);
		
		setSize(1000, 480);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}
	
	public void addPlayer(Client client) {
		playersList.addElement(client.getUserName());
		players.validate();
	}
	
	public void removePlayer(Client client) {
		playersList.removeElement(client.getUserName());
		players.validate();
	}
	
	public void write(String str) {
		logs.append("[" + new Date() + "] " + str + "\n");
	}
}