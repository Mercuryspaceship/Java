import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //listens to incoming connections
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader; //read messages from client
    private BufferedWriter bufferedWriter; //wraps the sockets to write messages to clients efficiently



    //takes server socket object(passed from controller)
    public Server(ServerSocket serverSocket){
        try {
        this.serverSocket = serverSocket; //listens for incoming connections
        this.socket = serverSocket.accept(); //communicate with incoming connection
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //read content from to whom we are connected to.it is wrapped in character stream
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            System.out.println("Error creating server.");
            e.printStackTrace();
        }
    }
    public void sendMessageToClient(String messageToClient){
        try {
            bufferedWriter.write(messageToClient);
            bufferedWriter.newLine();//"this is the end of the message"
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending message to the client");
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    //needs a separate thread
    public void receiveMessageFromClient(VBox vBox){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()){
                    try {
                        String messageFromClient = bufferedReader.readLine();
                        Controller.addLabel(messageFromClient,vBox);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving message from the client");
                        closeEverything(socket, bufferedWriter, bufferedReader);
                        break;
                    }

                }
            }
        }).start();
    }


    /* always close sockets and streams when you are not using them, to save resources */
    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
