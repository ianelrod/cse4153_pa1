// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Random;

public class server {
    public static void main(String[] args) // args include n_port
    {
        System.out.println("Hello World.");
        byte[] end = new byte[] {0x03};
        int n_port = Integer.parseInt(args[0]);
        int r_port = negotiation(n_port);
        String message = "";

        try {
            System.out.println("Creating socket on random port: " + r_port);
            DatagramSocket dsocket = new DatagramSocket(r_port);
            byte[] receive = new byte[Integer.BYTES];
            DatagramPacket dreceive = null;

            // iterate through file on socket
            do {
                try {
                    dreceive = new DatagramPacket(receive, receive.length);
                    dsocket.receive(dreceive);
                    String upper = Arrays.toString(dreceive.getData()).toUpperCase();
                    message = message.concat(Arrays.toString(dreceive.getData()));
                    DatagramPacket dsend = new DatagramPacket(upper.getBytes(), upper.getBytes().length, dreceive.getAddress(), dreceive.getPort());
                    dsocket.send(dsend);
                    System.out.println("Sent ACK.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } while (!Arrays.equals(dreceive.getData(), end));
            try {
                byte[] ack = new byte["ACK".getBytes().length];
                DatagramPacket dack = new DatagramPacket(ack, ack.length, dreceive.getAddress(), dreceive.getPort());
                dsocket.send(dack);
            } catch (IOException e) {
                e.printStackTrace();
            }
            dsocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        write(message);
    }

    static int negotiation(int n_port) {
        int min = 1024;
        int max = 65535;
        int handint = 1248;
        byte[] handbyte = new byte[] {
                (byte)(handint >>> 24),
                (byte)(handint >>> 16),
                (byte)(handint >>> 8),
                (byte)handint};
        Random rnd = new Random();
        int r_port = rnd.nextInt(max - min + 1) + min;
        byte[] send = new byte[] {
                (byte)(r_port >>> 24),
                (byte)(r_port >>> 16),
                (byte)(r_port >>> 8),
                (byte)r_port};

        // receive characters 1248 from client for handshake
        // send random port between 1024 and 65535 to client
        // write to screen: "Random port chosen: <r_port>"
        // once handshake complete, close negotiation socket

        try {
            System.out.println("Creating socket on negotiation port: " + n_port);
            DatagramSocket dsocket = new DatagramSocket(n_port);

            // send handshake
            try {
                byte[] receive = new byte[Integer.BYTES];
                DatagramPacket dreceive = new DatagramPacket(receive, receive.length);

                // receive 1248 handshake from client
                do {
                    System.out.println("Waiting for handshake...");
                    dsocket.receive(dreceive);   // change later
                    // dreceive.setData(handbyte);  // change later
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (!Arrays.equals(dreceive.getData(), handbyte));
                System.out.println("Received handshake.");
                DatagramPacket dsend = new DatagramPacket(send, send.length, dreceive.getAddress(), n_port);
                dsocket.send(dsend);
                System.out.println("Sent random port: " + r_port);
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

    String transaction(int r_port) {
        String chars = "";
        // use random port and serverip to receive file in UDP packets
        // each UDP packet contains chunks of 4 characters
        // acknowledge after each packet with payload in capital letters
        // wait for EOF
        return chars;
    }

    static void write(String message) {
        try {
            // delete upload.txt if it exists
            Files.deleteIfExists(FileSystems.getDefault().getPath("upload.txt"));
            // write to upload.txt
            Files.write(FileSystems.getDefault().getPath("upload.txt"), message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}