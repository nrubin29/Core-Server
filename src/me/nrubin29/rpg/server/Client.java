package me.nrubin29.rpg.server;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Client {

    private BufferedReader reader;
    private PrintWriter writer;
    private String userName;

    private int x, y;

    public Client(Socket socket) {
        try {
        	reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            this.userName = reader.readLine();

            Thread messageListener = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            String in = reader.readLine();

                            if (in == null) {
                                Server.getInstance().removeClient(Client.this);
                                break;
                            }

                            /*
                            PacketMove player:Noah direction:DOWN x:20 y:40
                             */
                            if (in.startsWith("PacketMove")) {
                                for (String str : in.split(" ")) {
                                    if (str.startsWith("x:")) x = Integer.parseInt(str.substring(2));
                                    else if (str.startsWith("y:")) y = Integer.parseInt(str.substring(2));
                                }

                                System.out.println("Updated " + getUserName() + " to position (" + x + "," + y + ")");
                            }

                            Server.getInstance().sendPacket(in, Client.this);
                        }
                        catch (SocketException e) { Server.getInstance().removeClient(Client.this); break; }
                        catch (EOFException e) { Server.getInstance().removeClient(Client.this); break; }
                        catch (Exception e) { e.printStackTrace(); /* TODO: for now. */ }
                    }
                }
            });

            messageListener.start();
            
            Server.getInstance().addClient(this);
        }
        catch (Exception e) { e.printStackTrace(); /* TODO: for now. */ }
    }

    public String getUserName() {
        return userName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void sendPacket(String packet) {
        try { writer.println(packet); }
        catch (Exception e) { e.printStackTrace(); }
    }
}