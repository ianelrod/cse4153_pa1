// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.Random;

public class server {
    public static void main(String[] args) // args include n_port
    {
        System.out.println("Hello World.");
        int n_port = Integer.parseInt(args[0]);
        int r_port = genRandomPort();
        String message = "";

        // get negotiation from client
        try {
            DatagramSocket dsneg = new DatagramSocket(n_port);
            byte[] ack = new byte[Integer.BYTES];
            System.out.println("Waiting for client handshake...");
            DatagramPacket dpnegrec = new DatagramPacket(ack, ack.length);
            do {
                dsneg.receive(dpnegrec);
            } while (byteArrayToInt(dpnegrec.getData()) != 1248);
            System.out.println("Received client handshake.");
            DatagramPacket dpnegsend = new DatagramPacket(intToByteArray(r_port), intToByteArray(r_port).length, dpnegrec.getAddress(), dpnegrec.getPort());
            send(dsneg, dpnegsend);
            System.out.println("Random port chosen: " + r_port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // iterate through file on socket
        byte[] end = new byte[] {0x03};
        try {
            System.out.println("Creating listening socket on random port: " + r_port);
            DatagramSocket dstrans = new DatagramSocket(r_port);
            DatagramPacket dptrans;
            do {
                dptrans = waittrans(dstrans);
                send(dstrans, dptrans);
                String chars = new String(dptrans.getData());
                System.out.println("Received chars: " + chars);
                message = message.concat(chars);
            } while (!Arrays.equals(dptrans.getData(), end));
            write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int genRandomPort() {
        Random r = new Random();
        int low = 1024;
        int high = 65536;
        return r.nextInt(high-low) + low;
    }

    private static DatagramPacket waittrans(DatagramSocket dsocket) throws IOException {
        String s = null;
        byte[] trans = new byte[8];
        DatagramPacket dtrans = new DatagramPacket(trans, trans.length);
        dsocket.receive(dtrans);
        return dtrans;
    }

    private static void send(DatagramSocket dsocket, DatagramPacket trans) {
        // pack bytes into UDP packet
        String s = new String(trans.getData()).toUpperCase();
        DatagramPacket dsend = new DatagramPacket(s.getBytes(), s.getBytes().length, trans.getAddress(), trans.getPort());

        // send over port and serverip
        try {
            System.out.println("Sending ACK: " + s);
            dsocket.send(dsend);
        } catch (IOException e) {
            System.out.println("Could not send packet.");
            e.printStackTrace();
        }
    }

    private static void write(String message) {
        try {
            // delete upload.txt if it exists
            Files.deleteIfExists(FileSystems.getDefault().getPath("upload.txt"));
            // write to upload.txt
            Files.write(FileSystems.getDefault().getPath("upload.txt"), message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int byteArrayToInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}