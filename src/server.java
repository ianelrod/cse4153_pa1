// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

public class server {
    public static void main(String[] args) // args include n_port
    {
        System.out.print("Hello World.");
        // negotiation();

        /* for loop to iterate
        extract();
        transaction();
        chars -> concat after each loop
        receive EOF, send last acknowledgement
        close ports
         */

        // write();
        // terminate
    }
    int negotiation(int n_port) {
        int r_port = 0;
        // receive characters 1248 from client for handshake
        // send random port between 1024 and 65535 to client
        // write to screen: "Random port chosen: <r_port>"
        // once handshake complete, close negotiation socket
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
    Object write(String concat) {
        Object upload = null;
        // Check if upload.txt exists
        // if exists, delete
        // write to upload.txt
        return upload;
    }
}