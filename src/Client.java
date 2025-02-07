import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket sendReceiveSocket;

    /**
     * Client constructor for the client application
     */
    public Client(){
        try {
            sendReceiveSocket = new DatagramSocket(); // start up the socket
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Prompts user to enter a name for their player
     * @return user's desired name
     */
    public String promptUsername(){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your player name: ");
        return s.nextLine();
    }

    /**
     * Enroll the player into the game
     * @param data the message size limit
     * @return the id of the newly enrolled player
     */
    public int enrollPlayer(byte[] data){
        String playerName = "JOIN:" + promptUsername(); // user inputs their desired username
        // convert to bytes to be sent in a UDP datagram packet
        byte[] msgPlayerName = playerName.getBytes();
        try { // attempt to create a datagram packet and send to host using port 5000
            sendPacket = new DatagramPacket(msgPlayerName, msgPlayerName.length,
                    InetAddress.getLocalHost(), 5000);
            sendReceiveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // showcase what the client sent
        String sent = new String(sendPacket.getData(),0,sendPacket.getLength());
        System.out.println("\nClient: sent:" +
                "\nTo host: " + sendPacket.getAddress() +
                "\nTo host port: " + sendPacket.getPort() +
                "\nLength: " + sendPacket.getLength() +
                "\nContaining: " + sent);

        receivePacket = new DatagramPacket(data, data.length); // prepare a datagram packet to receive from host

        try { // listen for datagram packet to be received from host
            sendReceiveSocket.receive(receivePacket);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // showcase what was received from host
        String received = new String(data,0,receivePacket.getLength());
        System.out.println("\nClient: received:" +
                "\nFrom host: " + receivePacket.getAddress() +
                "\nFrom host port: " + receivePacket.getPort() +
                "\nLength: " + receivePacket.getLength() +
                "\nContaining: " + received);
        
        // parse the message for the player id assigned by server
        String[] m = received.split(":");
        int playerId = Integer.parseInt(m[1]);
        System.out.println("Joined game with playerId = " + playerId);
        return playerId; // store player id
    }

    /**
     * Begin client application and establish connection with intermediate host
     */
    public void startClient(){
        byte[] data = new byte[1024]; // size of the message

        int playerId = enrollPlayer(data); // enroll player into the game

        while (true){ // infinite loop until user enters 'quit'
            // listen for user's commands in terminal
            Scanner s = new Scanner(System.in);
            System.out.println("Commands: MOVE dx dy | PICKUP lootId | STATE | QUIT");
            System.out.println("Enter your command: ");
            String command = s.nextLine().toUpperCase(); // convert to upper case to be processed properly

            // parse the command for specific instructions
            String[] processCommand = command.split(" ");
            if (Objects.equals(processCommand[0], "MOVE")){
                command = String.format("%s:%d:%s:%s",  processCommand[0], playerId, processCommand[1], processCommand[2]);
            } else if (Objects.equals(processCommand[0], "PICKUP")) {
                command = String.format("%s:%d:%s", processCommand[0], playerId, processCommand[1]);
            } // 'state' and 'quit' do not require player id

            // turn new command into bytes to be inserted into a datagram packet and sent to host
            byte[] msg = command.getBytes();
            try { // send UDP datagram packet containing command to host
                sendPacket = new DatagramPacket(msg, msg.length,
                        InetAddress.getLocalHost(), 5000);
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // showcase what was sent
            String clientSent = new String(sendPacket.getData(),0,sendPacket.getLength());

            System.out.println("\nClient: sent:" +
                    "\nTo host: " + sendPacket.getAddress() +
                    "\nTo host port: " + sendPacket.getPort() +
                    "\nLength: " + sendPacket.getLength() +
                    "\nContaining: " + clientSent);

            // close socket and end process if user wanted to quit
            if (Objects.equals(clientSent, "QUIT")) {
                sendReceiveSocket.close();
                System.exit(0);
            } // quit request will have been sent to host and server, to which handle their own shutdown

            // prepare datagram packet for receiving from host
            receivePacket = new DatagramPacket(data, data.length);

            try { // receive message from host
                sendReceiveSocket.receive(receivePacket);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            // showcase what was received from host
            String clientReceived = new String(data,0,receivePacket.getLength());
            System.out.println("\nClient: received:" +
                    "\nFrom host: " + receivePacket.getAddress() +
                    "\nFrom host port: " + receivePacket.getPort() +
                    "\nLength: " + receivePacket.getLength() +
                    "\nContaining: " + clientReceived);
        }

    }

    /**
     * Main method
     * @param args args
     */
    public static void main(String[] args) {
        Client c = new Client(); // make a client instance and start it
        c.startClient();
    }
}
