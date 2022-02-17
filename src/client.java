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
            DatagramSocket dneg = new DatagramSocket();
            byte[] send = new byte[] {0x00,0x00,0x04,(byte)0xE0};
            send(dneg, send, serverip, n_port);
            try {
                byte[] receive = new byte[Integer.BYTES];
                DatagramPacket dreceive = new DatagramPacket(receive,receive.length);
                dneg.receive(dreceive);
                r_port = byteArrayToInt(dreceive.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            dneg.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        // declare and get list
        List <String> list = convert(path);

        // create random port socket
        try {
            DatagramSocket dtrans = new DatagramSocket();
            System.out.println("Creating transfer socket on port: " + dtrans.getPort());

            // iterate through file on socket
            for (int i = 0; i < list.size(); i++) {
                try {
                    System.out.println("Sending packet: " + list.get(i));
                    byte[] send = list.get(i).getBytes();
                    byte[] receive = new byte[list.get(i).toUpperCase().getBytes().length];
                    send(dtrans, send, serverip, r_port);
                    String s = waitack(dtrans, receive);
                    System.out.println(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // send EOF
            try {
                byte[] send = new byte[] {0x03};
                byte[] receive = new byte["ACK".getBytes().length];
                System.out.println("Sending EOF...");
                send(dtrans, send, serverip, r_port);
                waitack(dtrans, receive);
            } catch (IOException e) {
                System.out.println("Could not send EOF.");
                e.printStackTrace();
            }
            dtrans.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    static void send(DatagramSocket dsocket, byte[] send, InetAddress serverip, int port) {
        // pack bytes into UDP packet
        DatagramPacket dsend = new DatagramPacket(send, send.length, serverip, port);
        String s = new String(send);

        // send over port and serverip
        try {
            System.out.println("Sending packet: " + s);
            dsocket.send(dsend);
        } catch (IOException e) {
            System.out.println("Could not send packet.");
            e.printStackTrace();
        }
    }

    private static String waitack(DatagramSocket dsocket, byte[] response) throws IOException {
        byte[] ack = new byte[response.length];
        DatagramPacket dack = new DatagramPacket(ack, ack.length);
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
        return new String(dack.getData());
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

        //return list
        return list;
    }

    static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }
}