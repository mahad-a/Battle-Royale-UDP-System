import java.io.*;
import java.net.*;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket sendReceiveSocket;

    public Client(){
        try {
            sendReceiveSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public String promptUsername(){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your player name: ");
        return s.nextLine();
    }

    public void startClient(){
        String playerName = "JOIN:" + promptUsername();
        System.out.println(playerName);
        byte[] msgPlayerName = playerName.getBytes();
        try {
            sendPacket = new DatagramPacket(msgPlayerName, msgPlayerName.length,
                    InetAddress.getLocalHost(), 5000);
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println();
        System.out.println("Client: sent:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("To host port: " + sendPacket.getPort());
        System.out.println("Length: " + sendPacket.getLength());
        System.out.print("Containing: ");
        System.out.println(new String(sendPacket.getData(),0,sendPacket.getLength()));


        byte[] data = new byte[1024];
        receivePacket = new DatagramPacket(data, data.length);

        try {
            // Block until a datagram is received via sendReceiveSocket.
            sendReceiveSocket.receive(receivePacket);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println();
        System.out.println("Client: received:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("From host port: " + receivePacket.getPort());
        System.out.println("Length: " + receivePacket.getLength());
        System.out.print("Containing: ");

        // Form a String from the byte array.
        String received = new String(data,0,receivePacket.getLength());
        System.out.println(received);
        String[] m = received.split(":");
        int playerId = Integer.parseInt(m[1]);
        System.out.println("Joined game with playerId = " + playerId);

        while (true){
            Scanner s = new Scanner(System.in);
            System.out.println("Commands: MOVE dx dy | PICKUP lootId | STATE | QUIT");
            System.out.println("Enter your command: ");
            String prompt = s.nextLine().toUpperCase();

            String[] processPrompt = prompt.split(" ");
            if (Objects.equals(processPrompt[0], "MOVE")){
                prompt = String.format("%s:%d:%s:%s",  processPrompt[0], playerId, processPrompt[1], processPrompt[2]);
            } else if (Objects.equals(processPrompt[0], "PICKUP")) {
                prompt = String.format("%s:%d:%s", processPrompt[0], playerId, processPrompt[1]);
            } else if (Objects.equals(processPrompt[0], "QUIT")) {
                System.exit(1);
            }
            byte[] msg = prompt.getBytes();
            try {
                sendPacket = new DatagramPacket(msg, msg.length,
                        InetAddress.getLocalHost(), 5000);
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println();
            System.out.println("Client: sent:");
            System.out.println("To host: " + sendPacket.getAddress());
            System.out.println("To host port: " + sendPacket.getPort());
            System.out.println("Length: " + sendPacket.getLength());
            String clientSent = new String(sendPacket.getData(),0,sendPacket.getLength());
            System.out.print("Containing: " + clientSent);

            receivePacket = new DatagramPacket(data, data.length);

            try {
                sendReceiveSocket.receive(receivePacket);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println();
            System.out.println("Client: received:");
            System.out.println("From host: " + receivePacket.getAddress());
            System.out.println("From host port: " + receivePacket.getPort());
            System.out.println("Length: " + receivePacket.getLength());
            String clientReceived = new String(data,0,receivePacket.getLength());
            System.out.print("Containing: " + clientReceived);
            System.out.println();
        }

    }

    public static void main(String[] args) {
        Client c = new Client();
        c.startClient();
    }
}
