import java.io.*;
import java.net.*;

public class Server {
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket sendReceiveSocket;
    private final GameState gameState = new GameState();

    public Server(){
        try {
            sendReceiveSocket = new DatagramSocket(6000);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    public void startServer(){
        byte[] data = new byte[1024];
        receivePacket = new DatagramPacket(data, data.length);

        while (true){
            try {
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Server: received: ");
            System.out.println("From host: " + receivePacket.getAddress());
            System.out.println("From host port: " + receivePacket.getPort());
            System.out.println("Length: " + receivePacket.getLength());

            System.out.println("Containing: ");
            String serverReceived = new String(data,0,receivePacket.getLength());
            System.out.println(serverReceived);
            String response = processRequest(serverReceived);

            sendPacket = new DatagramPacket(response.getBytes(), response.getBytes().length,
                    receivePacket.getAddress(), receivePacket.getPort());

            try {
                sendReceiveSocket.send(sendPacket);
                System.out.println("Sent: " + response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public String processRequest(String message){
        String[] m = message.split(":");
        return switch (m[0]) {
            case "JOIN" -> {
                Player newPlayer = gameState.addNewPlayer(message.substring(5));
                yield "JOINED:" + newPlayer.getId();
            }
            case "MOVE" -> {
                gameState.movePlayer(Integer.parseInt(m[1]), Integer.parseInt(m[2]), Integer.parseInt(m[3]));
                yield "MOVE_OK";
            }
            case "PICKUP" -> {
                if (gameState.processPickup(Integer.parseInt(m[1]), Integer.parseInt(m[2]))) yield "PICKUP_OK";
                yield "PICKUP_FAIL";
            }
            case "STATE" -> gameState.serialize();
            default -> "NOT_A_COMMAND";
        };
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
