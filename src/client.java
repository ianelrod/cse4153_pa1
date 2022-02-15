// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
    int negotiation(String serverip, String n_port) {
        // send the characters 1248 to initiate a handshake
        // receive r_port between 1024 and 65535 from server
        // once handshake complete, close negotiation socket
        return 0;
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