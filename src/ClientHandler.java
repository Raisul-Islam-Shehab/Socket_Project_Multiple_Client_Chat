import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {   // each instance will start a new thread
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();  // to send msg to all clients
    private Socket socket;
    private BufferedReader bufferedReader;  // to read data
    private BufferedWriter bufferedWriter;  // to write data
    private String clientUserName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // converting byte stream to character stream
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = bufferedReader.readLine();
            clientHandlers.add(this);  // add new client to the clientHandlers arraylist
            broadCastMessage("Server: " + clientUserName + " has joined the chat");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();  // takes a newline to stop
                broadCastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadCastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(this.clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();  // indicates that message sending is finished
                    clientHandler.bufferedWriter.flush();  // makes the bufferedWriter full manually
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
        public void removeClientHandler () {
            clientHandlers.remove(this);
            broadCastMessage("Server : " + clientUserName + " has left");
        }

        public void closeEverything (Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
            removeClientHandler();
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
