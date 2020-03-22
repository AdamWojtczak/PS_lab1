import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Scanner;

import static java.lang.Thread.sleep;

class TCPClient {
    static Socket clientSocket = null;
    static DataOutputStream outToServer = null;
    static BufferedReader inFromUser = null;
    static DataInputStream inFromServer = null;
    static boolean global_flag_end = true;
    static Scanner scanner = null;

    public static int getPort() {
        System.out.println("Listening to port 7 by standard, do you want to change it?: (y/n) ");
        String answer = scanner.nextLine();
        int port = 7;
        if (answer.equals("y") || answer.equals("Y")) {
            System.out.println("Insert port number: ");
            port = scanner.nextInt();
        }
        return port;
    }

    public static void startConnection(String ip){
        int port = 7;
        try {
            scanner = new Scanner(System.in);
            port = getPort();
        } catch(Exception e)
        {
            System.out.println("Error caught while getting port");
        }
        try {
            clientSocket = new Socket(ip, port);
        } catch(Exception e) {
            System.out.println("Error caught while creating socket");
            stopConnection();
        }
        try {
            inFromUser = new BufferedReader(new InputStreamReader(System.in));
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            inFromServer = new DataInputStream(clientSocket.getInputStream());
        } catch ( Exception e) {
            System.out.println("Error caught while creating streams");
        }
    }

    public static int sendMessage() throws IOException {
        System.out.println("What do you want to say to the server: ");
        String sentence = "";
        while (sentence.length() == 0) {
            sentence = inFromUser.readLine();
            if (sentence.length() == 0) {
                System.out.println("Msg not written");
            }
        }
        System.out.println("Sent bytes " + sentence.length());
        outToServer.writeBytes(sentence);
        return sentence.length();
    }

    public static void getMessage(int bytesSent) throws IOException {
        String messageReaded = "";
        int bytesReaded = 0;
        while(inFromServer.available() != 0 || bytesReaded == 0) {
            byte[] bytes = new byte[1024];
            try {
                sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            bytesReaded = inFromServer.read(bytes);
            messageReaded = new String(bytes, 0, bytesReaded);
        }
        System.out.println("Received bytes: " + bytesReaded);
        System.out.println("Msg from server: " + messageReaded);
        if (messageReaded.equals("end"))
            stopConnection();
    }

    public static void stopConnection(){
        global_flag_end = false;
        try {
            if (inFromUser != null)
                inFromUser.close();
            if (inFromServer != null)
                inFromServer.close();
            if (outToServer != null)
                outToServer.close();
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error caught while closing streams and socket");
        }

    }
    public static void main(String argv[]) throws Exception {
        startConnection("localhost");
        while(global_flag_end) {
            int bytesSent = sendMessage();
            getMessage(bytesSent);
        }
    }
}
