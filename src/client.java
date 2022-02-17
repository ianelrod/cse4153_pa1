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
        int r_port = 0;

        // get r_port
        try {
            DatagramSocket dsneg = new DatagramSocket();
            byte[] send = new byte[] {0x00,0x00,0x04,(byte)0xE0};
            System.out.println("Sending handshake...");
            send(dsneg, send, serverip, n_port);
            try {
                byte[] receive = new byte[Integer.BYTES];
                DatagramPacket dreceive = new DatagramPacket(receive,receive.length);
                dsneg.receive(dreceive);
                r_port = byteArrayToInt(dreceive.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Received random port: " + r_port);
            dsneg.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // declare and get list
        List <String> list = convert(path);

        // create random port socket
        try {
            DatagramSocket dstrans = new DatagramSocket();
            System.out.println("Creating transfer socket on port: " + r_port);

            // iterate through file on socket
            for (int i = 0; i < list.size(); i++) {
                try {
                    System.out.println("Sending packet: " + list.get(i));
                    byte[] send = list.get(i).getBytes();
                    byte[] receive = list.get(i).toUpperCase().getBytes();
                    send(dstrans, send, serverip, r_port);
                    String s = waitack(dstrans, receive);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // send EOF
            byte[] send = new byte[] {0x03};
            System.out.println("Sending EOF...");
            send(dstrans, send, serverip, r_port);
            dstrans.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private static void send(DatagramSocket dsocket, byte[] send, InetAddress serverip, int port) {
        // pack bytes into UDP packet
        DatagramPacket dsend = new DatagramPacket(send, send.length, serverip, port);
        String s = new String(send);

        // send over port and serverip
        try {
            dsocket.send(dsend);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String waitack(DatagramSocket dsocket, byte[] response) throws IOException {
        byte[] ack = new byte[response.length];
        DatagramPacket dack = new DatagramPacket(ack, ack.length);
        String test = null;
        String resp = new String(response);

        do {
            System.out.println("Waiting for ACK...");
            dsocket.receive(dack);
            test = new String(dack.getData()).toUpperCase();
        } while (!(Objects.equals(test, resp)));
        System.out.println("Received ACK.");
        return test;
    }

    private static List<String> convert(Path path) { // Convert file to List of 4 char 8-bit ASCII
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

        //return list
        return list;
    }

    private static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }
}