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

    public String sendRequest(String receiver){
        String sent = new String(sendPacket.getData(),0,sendPacket.getLength());
        System.out.println("\nClient: sent:" +
                "\nTo " + receiver + ": " + sendPacket.getAddress() +
                "\nTo " + receiver + " port: " + sendPacket.getPort() +
                "\nLength: " + sendPacket.getLength() +
                "\nContaining: " + sent);
        return sent;
    }

    public String receiveRequest(String sender, byte[] data){
        String received = new String(data,0,receivePacket.getLength());
        System.out.println("\nClient: received:"+
                "\nFrom " + sender + ": " + receivePacket.getAddress() +
                "\nFrom " + sender + " port: " + receivePacket.getPort() +
                "\nLength: " + receivePacket.getLength() +
                "\nContaining: " + received);
        return received;
    }

    public int playerEnrollment(byte[] data){
        String playerName = "JOIN:" + promptUsername();
        // System.out.println(playerName);
        byte[] msgPlayerName = playerName.getBytes();
        try {
            sendPacket = new DatagramPacket(msgPlayerName, msgPlayerName.length,
                    InetAddress.getLocalHost(), 5000);
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        sendRequest("host");

        try {
            sendReceiveSocket.receive(receivePacket);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        String receivedJoin = receiveRequest("host", data);

        String[] m = receivedJoin.split(":");
        int playerId = Integer.parseInt(m[1]);
        System.out.println("Joined game with playerId = " + playerId);
        return playerId;
    }

    public void startClient(){
        byte[] data = new byte[1024];
        receivePacket = new DatagramPacket(data, data.length);

        int playerId = playerEnrollment(data);

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

            String clientSent = sendRequest("host");

            if (Objects.equals(clientSent, "QUIT")) {
                sendReceiveSocket.close();
                System.exit(1);
            }

            receivePacket = new DatagramPacket(data, data.length);

            try {
                sendReceiveSocket.receive(receivePacket);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            receiveRequest("host", data);
        }

    }

    public static void main(String[] args) {
        Client c = new Client();
        c.startClient();
    }
}
