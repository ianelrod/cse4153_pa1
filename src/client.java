// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class client {
    public static void main(String[] args) // args include serverip, n_port, file
    {
        System.out.print("Hello World.");
        try {
            final InetAddress serverip = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int n_port = Integer.parseInt(args[1]);
        Path path = FileSystems.getDefault().getPath(args[2]);

        // negotiation();
        // convert();

        /* for loop to iterate
        grab next 4 8-bit ASCII chars from List
        transaction();
        if finished, send EOF
        wait for server to acknowledge
        close ports
        */

        // terminate
    }
    int negotiation(int n_port, InetAddress serverip) {
        // send the characters 1248 to initiate a handshake
        // receive r_port between 1024 and 65535 from server
        // once handshake complete, close negotiation socket
        DatagramSocket dsocket = null;
        try {
            dsocket = new DatagramSocket(n_port, serverip);
        } catch (SocketException e) {
            System.out.println("Could not create socket.");
            e.printStackTrace();
        }

        byte[] send = ByteBuffer.allocate(Integer.BYTES).putInt(1248).array();
        DatagramPacket dsend = new DatagramPacket(send, send.length, serverip, n_port);
        try {
            dsocket.send(dsend);
        } catch (IOException e) {
            System.out.println("Could not send handshake.");
            e.printStackTrace();
        }

        byte[] receive = new byte[Integer.BYTES];
        DatagramPacket dreceive = new DatagramPacket(receive, receive.length);
        try {
            dsocket.receive(dreceive);
        } catch (IOException e) {
            System.out.println("Could not receive port.");
            e.printStackTrace();
        }

        int r_port = ByteBuffer.allocate(Integer.BYTES).put(dreceive.getData()).getInt();
        return r_port;
    }

    List<String> convert(Path path) { // Convert file to List of 4 char 8-bit ASCII
        List<String> list = new ArrayList<>();
        String message = null;

        try {
            message = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < Objects.requireNonNull(message).length(); i += 4) {
            list.add((message.substring(i, Math.min(i + 4, message.length()))));
        }

        return list;
    }
    void transaction(List<String> list, String serverip, String r_port) {
        // pack 4 characters into UDP packet
        // send over random port and serverip
        // send EOF at end of file
    }
}