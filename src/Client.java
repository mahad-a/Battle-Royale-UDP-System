import java.io.*;
import java.net.*;
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

        System.out.println("Client: received:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("From host port: " + receivePacket.getPort());
        System.out.println("Length: " + receivePacket.getLength());
        System.out.print("Containing: ");

        // Form a String from the byte array.
        String received = new String(data,0,receivePacket.getLength());
        System.out.println(received);

        while (true){
            Scanner s = new Scanner(System.in);
            System.out.println("Enter your command: ");
            String prompt = s.nextLine();
            byte[] msg = prompt.getBytes();
            try {
                sendPacket = new DatagramPacket(msg, msg.length,
                        InetAddress.getLocalHost(), 5000);
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("Client: sent:");
            System.out.println("To host: " + sendPacket.getAddress());
            System.out.println("To host port: " + sendPacket.getPort());
            System.out.println("Length: " + sendPacket.getLength());
            System.out.print("Containing: ");
            System.out.println(new String(sendPacket.getData(),0,sendPacket.getLength()));

            byte[] data2 = new byte[1024];
            receivePacket = new DatagramPacket(data2, data2.length);

            try {
                sendReceiveSocket.receive(receivePacket);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("Client: received:");
            System.out.println("From host: " + receivePacket.getAddress());
            System.out.println("From host port: " + receivePacket.getPort());
            System.out.println("Length: " + receivePacket.getLength());
            System.out.print("Containing: ");
        }

    }

    public static void main(String[] args) {
        Client c = new Client();
        c.startClient();
    }
}
