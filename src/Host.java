import java.io.*;
import java.net.*;

public class Host {
    DatagramPacket clientSendPacket, clientReceivePacket, serverSendPacket, serverReceivePacket;
    DatagramSocket clientSocket, serverSocket;

    public Host() {
        try{
            clientSocket = new DatagramSocket(5000);
            serverSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void startHost(){
        byte[] data = new byte[1024];
        clientReceivePacket = new DatagramPacket(data, data.length);
        serverReceivePacket = new DatagramPacket(data, data.length);

        while (true){
            try {
                clientSocket.receive(clientReceivePacket);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println("Host: received: ");
            System.out.println("From client: " + clientReceivePacket.getAddress());
            System.out.println("From client port: " + clientReceivePacket.getPort());
            System.out.println("Length: " + clientReceivePacket.getLength());
            System.out.print("Containing: ");

            String clientReceived = new String(data,0,clientReceivePacket.getLength());
            System.out.println(clientReceived);

            try {
                serverSendPacket = new DatagramPacket(clientReceived.getBytes(), clientReceived.getBytes().length,
                        InetAddress.getLocalHost(), 6000);
                serverSocket.send(serverSendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println();
            System.out.println("Host: forwarded:");
            System.out.println("To server: " + serverSendPacket.getAddress());
            System.out.println("To server port: " + serverSendPacket.getPort());
            System.out.println("Length: " + serverSendPacket.getLength());
            System.out.print("Containing: ");
            System.out.println(new String(serverSendPacket.getData(),0,serverSendPacket.getLength()));


            try {
                serverSocket.receive(serverReceivePacket);
            } catch(IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            System.out.println();
            System.out.println("Host: received: ");
            System.out.println("From server: " + serverReceivePacket.getAddress());
            System.out.println("From server port: " + serverReceivePacket.getPort());
            System.out.println("Length: " + serverReceivePacket.getLength());
            System.out.print("Containing: ");

            String serverReceived = new String(data,0,serverReceivePacket.getLength());
            System.out.println(serverReceived);

            try {
                clientSendPacket = new DatagramPacket(serverReceived.getBytes(), serverReceived.getBytes().length,
                        clientReceivePacket.getAddress(), clientReceivePacket.getPort());
                clientSocket.send(clientSendPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println();
            System.out.println("Host: forwarded:");
            System.out.println("To client: " + clientSendPacket.getAddress());
            System.out.println("To client port: " + clientSendPacket.getPort());
            System.out.println("Length: " + clientSendPacket.getLength());
            System.out.print("Containing: ");
            System.out.println(new String(clientSendPacket.getData(),0,clientSendPacket.getLength()));

        }
    }

    public static void main(String[] args) {
        Host host = new Host();
        host.startHost();
    }
}
