// Name: Ian Goforth
// Email: img56@msstate.edu
// Student ID: 902-268-372

public class client {
    public static void main(String[] args) // args include serverip, n_port, file
    {
        System.out.print("Hello World.");
        // negotiation();

        /* for loop to iterate
        extract();
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
    String extract(Object file, int position) {
        String chars = "";
        // extract 4 characters from file and return
        return chars;
    }
    void transaction(String chars, String serverip, String r_port) {
        // pack 4 characters into UDP packet
        // send over random port and serverip
        // send EOF at end of file
    }
}