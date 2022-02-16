// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public class client {
    public static void main(String[] args) // args include serverip, n_port, file
    {
        System.out.print("Hello World.");

        // declare variable and parse args
        InetAddress serverip = null;
        try {
            serverip = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.out.println("Invalid Server IP.");
            e.printStackTrace();
        }
        int n_port = Integer.parseInt(args[1]);
        Path path = FileSystems.getDefault().getPath(args[2]);

        // get r_port
        int r_port = negotiation(n_port, serverip);

        // declare and get list
        List <String> list = convert(path);

        // create random port socket
        try {
            System.out.println("Creating socket on random port: " + r_port);
            DatagramSocket dsocket = new DatagramSocket(r_port, serverip);

            // iterate through file on socket
            for (int i = 0; i < list.size(); i++) {
                transaction(dsocket, list, serverip, r_port, i);
            }

            // send EOF
            byte[] end = new byte[] {0x03};
            DatagramPacket dend = new DatagramPacket(end, end.length, serverip, r_port);
            try {
                System.out.println("Sending EOF...");
                dsocket.send(dend);

                // receive ACK to terminate
                byte[] term = new byte["ACK".getBytes().length];
                DatagramPacket dterm = new DatagramPacket(term, term.length);
                try {
                    System.out.println("Waiting for ACK...");
                    dsocket.receive(dterm);

                    String s = new String(dterm.getData());
                    if (Objects.equals(s, "ACK")) {
                        System.out.println("Received ACK, terminating.");
                        dsocket.close();
                    }
                    else {
                        System.out.println("Did not receive ACK to terminate.");
                    }
                } catch (IOException e) {
                    System.out.println("Could not receive ACK.");
                    e.printStackTrace();
                }
            } catch (IOException e) {
                System.out.println("Could not send EOF.");
                e.printStackTrace();
            }
        } catch (SocketException e) {
            System.out.println("Could not create socket.");
            e.printStackTrace();
        }
    }
    static int negotiation(int n_port, InetAddress serverip) {
        int r_port = 0;

        // create socket on port
        try {
            System.out.println("Creating socket on negotiation port: " + n_port);
            DatagramSocket dsocket = new DatagramSocket(n_port, serverip);

            // send handshake
            try {
                byte[] send = ByteBuffer.allocate(Integer.BYTES).putInt(1248).array();
                DatagramPacket dsend = new DatagramPacket(send, send.length, serverip, n_port);
                byte[] receive = new byte[Integer.BYTES];
                DatagramPacket dreceive = new DatagramPacket(receive, receive.length);

                while (dreceive.getData() == null) {
                    System.out.println("Sending handshake: 1248");
                    dsocket.send(dsend);

                    // receive r_port packet between 1024 and 65535 from server
                    try {
                        dsocket.receive(dreceive);
                        if (dreceive.getData() != null) {
                            String s = new String(dreceive.getData());
                            System.out.println("Received random port: " + s);

                            // map packet to r_port, close, and return
                            r_port = ByteBuffer.allocate(Integer.BYTES).put(dreceive.getData()).getInt();
                        }
                    } catch (IOException e) {
                        System.out.println("Could not receive port.");
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Could not send handshake.");
                e.printStackTrace();
            }
            dsocket.close();
        } catch (SocketException e) {
            System.out.println("Could not create socket.");
            e.printStackTrace();
        }
        return r_port;
    }

    static List<String> convert(Path path) { // Convert file to List of 4 char 8-bit ASCII
        List<String> list = new ArrayList<>();
        String message = null;

        // read message from file
        try {
            System.out.println("Reading message from file...");
            message = Files.readString(path);
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
    static void transaction(DatagramSocket dsocket, List<String> list, InetAddress serverip, int r_port, int count) {
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