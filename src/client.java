// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
public class client {
    public static void main(String[] args) // args include serverip, n_port, file
    {
        System.out.println("Hello World.");

        // declare variable and parse args
        InetAddress serverip = null;
        try {
            serverip = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("Invalid Server IP.");
            e.printStackTrace();
        }
        int n_port = Integer.parseInt(args[1]);
        Path path = FileSystems.getDefault().getPath(args[2]);    // change later

        // get r_port
        int r_port = negotiation(n_port, serverip);

        // declare and get list
        List <String> list = convert(path);

        // create random port socket
        try {
            System.out.println("Creating socket on random port: " + r_port);
            DatagramSocket dsocket = new DatagramSocket();

            // iterate through file on socket
            for (int i = 0; i < list.size(); i++) {
                try {
                    send(dsocket, list, serverip, r_port, i);
                    byte[] ack = new byte[list.get(i).getBytes().length];
                    DatagramPacket dack = new DatagramPacket(ack, ack.length);
                    waitack(dsocket, dack, list.get(i).toUpperCase().getBytes());
                    System.out.println(Arrays.toString(dack.getData()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // send EOF
            byte[] end = new byte[] {0x03};
            DatagramPacket dend = new DatagramPacket(end, end.length, serverip, r_port);
            try {
                System.out.println("Sending EOF...");
                dsocket.send(dend);
                byte[] ack = new byte["ACK".getBytes().length];
                DatagramPacket dack = new DatagramPacket(ack, ack.length);
                waitack(dsocket, dack, "ACK".getBytes());
            } catch (IOException e) {
                System.out.println("Could not send EOF.");
                e.printStackTrace();
            }
            dsocket.close();
        } catch (SocketException e) {
            System.out.println("Could not create socket.");
            e.printStackTrace();
        }
    }

    private static void waitack(DatagramSocket dsocket, DatagramPacket dack, byte[] response) throws IOException {
        do {
            dsocket.receive(dack);
            System.out.println("Waiting for ACK...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!Arrays.equals(dack.getData(), response));
        System.out.println("Received ACK.");
    }

    static int negotiation(int n_port, InetAddress serverip) {
        int r_port = 0;

        // create socket on port
        try {
            System.out.println("Creating socket on negotiation port: " + n_port);
            DatagramSocket dsocket = new DatagramSocket();

            // send handshake
            try {
                byte[] send = new byte[] {0x00,0x00,0x04,(byte)0xE0}; // HexFormat.of().parseHex("000004E0");
                DatagramPacket dsend = new DatagramPacket(send, send.length, serverip, n_port);
                byte[] receive = new byte[Integer.BYTES];
                DatagramPacket dreceive = new DatagramPacket(receive, receive.length);

                // receive r_port packet between 1024 and 65535 from server
                do {
                    System.out.println("Sending handshake: 1248");
                    dsocket.send(dsend);
                    System.out.println("Waiting for port...");
                    dsocket.receive(dreceive);
                    // dreceive.setData(HexFormat.of().parseHex("00001000"));  // change later
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (byte b : dreceive.getData()) {
                        r_port = (r_port << 8) + (b & 0xFF);
                    }
                } while (!((r_port > 1024) && (r_port < 65535) && (r_port != 1248)));
                System.out.println("Received random port: " + r_port);
            } catch (IOException e) {
                System.out.println("Could not send handshake.");
                e.printStackTrace();
            }
            // close socket
            dsocket.close();
        } catch (SocketException e) {
            System.out.println("Could not create socket.");
            e.printStackTrace();
        }
        // return r_port
        return r_port;
    }

    static List<String> convert(Path path) { // Convert file to List of 4 char 8-bit ASCII
        List<String> list = new ArrayList<>();
        String message = null;

        // read message from file
        try {
            System.out.println("Reading message from file...");
            message = Files.readString(path.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Could not read message from file.");
            e.printStackTrace();
        }

        // add message to list of strings 4 chars long
        for (int i = 0; i < Objects.requireNonNull(message).length(); i += 4) {
            list.add((message.substring(i, Math.min(i + 4, message.length()))));
        }

        return list;
    }

    static void send(DatagramSocket dsocket, List<String> list, InetAddress serverip, int r_port, int count) {
        // pack 4 characters into UDP packet
        byte[] send = list.get(count).getBytes(StandardCharsets.UTF_8);
        DatagramPacket dsend = new DatagramPacket(send, send.length, serverip, r_port);

        // send over random port and serverip
        try {
            System.out.println("Sending packet: " + list.get(count));
            dsocket.send(dsend);
        } catch (IOException e) {
            System.out.println("Could not send packet.");
            e.printStackTrace();
        }
    }
}