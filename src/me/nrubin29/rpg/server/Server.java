package me.nrubin29.rpg.server;

import java.net.BindException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Server {

    private Server() {
        new Thread(new Runnable() {
            public void run() {
                Scanner s = new Scanner(System.in);
                while (s.hasNext()) {
                    String line = s.nextLine();
                    if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("stop")) System.exit(0);
                }
            }
        }).start();
    }

    private static Server instance = new Server();

    public static Server getInstance() {
        return instance;
    }

    private GUI gui;
    private String map;
    private ServerSocket server;
    private ArrayList<Client> users = new ArrayList<Client>();

    public void start() {
        try {
        	server = getOpenSocket();
            
            gui = new GUI(server.getLocalPort());

            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try { new Client(server.accept()); }
                        catch (Exception e) { e.printStackTrace(); /* TODO: for now. */ }
                    }
                }
            }).start();
        }
        catch (Exception e) { e.printStackTrace(); /* TODO: for now. */ }
    }
    
    void setMap(String map) {
    	this.map = map;
    }

    private ServerSocket getOpenSocket() {
        try {
            Random r = new Random();
            int rPort = r.nextInt(65535);
            if (rPort < 1000) return getOpenSocket();
            return new ServerSocket(rPort, 100);
        }
        catch (StackOverflowError e) { System.exit(1); }
        catch (BindException e) { return getOpenSocket(); }
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }
    
    public void sendPacket(String packet, Client sender) {
    	gui.write(packet);
    	for (Client client : users) {
    		if (client != sender) client.sendPacket(packet);
    	}
    }

    public void sendPacketToPlayer(String packet, Client player) {
        gui.write(packet + " to " + player.getUserName());
        player.sendPacket(packet);
    }

    public void addClient(Client client) {
        users.add(client);
        gui.addPlayer(client);

        StringBuffer players = new StringBuffer("PacketListPlayers players:");

        for (Client player : users) {
            players.append(player.getUserName() + "%" + player.getX() + "%" + player.getY() + ",");
        }

        sendPacketToPlayer(players.toString(), client);
        
        sendPacketToPlayer("PacketMap xml:" + map + " renderNow:true", client);
    }

    public void removeClient(Client client) {
        users.remove(client);
        gui.removePlayer(client);
    }

    public static void main(String[] args) {
        Server.getInstance().start();
    }
}