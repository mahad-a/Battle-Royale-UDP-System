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
            String hostReceivedClient = new String(data,0,clientReceivePacket.getLength());

            System.out.print("Containing: " + hostReceivedClient);

            try {
                serverSendPacket = new DatagramPacket(hostReceivedClient.getBytes(), hostReceivedClient.getBytes().length,
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
            String hostForwardServer = new String(serverSendPacket.getData(),0,serverSendPacket.getLength());
            System.out.print("Containing: " + hostForwardServer);
            System.out.println();


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
            String hostReceivedServer = new String(data,0,serverReceivePacket.getLength());
            System.out.print("Containing: " + hostReceivedServer);

            try {
                clientSendPacket = new DatagramPacket(hostReceivedServer.getBytes(), hostReceivedServer.getBytes().length,
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
            String hostForwardClient = new String(clientSendPacket.getData(),0,clientSendPacket.getLength());
            System.out.print("Containing: " + hostForwardClient);

        }
    }

    public static void main(String[] args) {
        Host host = new Host();
        host.startHost();
    }
}
