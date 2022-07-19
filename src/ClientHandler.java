import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientHandler implements Runnable {

    //static so it belongs to cle class
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); //a list of all clients
    private Socket socket;
    private BufferedReader bufferedReader; // to read data
    private BufferedWriter bufferedWriter; // to send data
    private String clientUsername;
    private String clientPass;
    private String clientPurpose;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); //send things
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //read things
            this.clientUsername = bufferedReader.readLine();
            this.clientPass = bufferedReader.readLine();
            this.clientPurpose = bufferedReader.readLine();
                checkUsers(this.clientPurpose, this.clientUsername, this.clientPass);
                //clientHandlers.add(this); //represents a clientHandler object to add to the array of clients
                broadcastMessage("SERVER: " + clientUsername + " is online");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        //listening for messages is a blocking thing
        String messageFromClient;
        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    public void checkUsers(String clientPurpose, String clientUsername, String clientPass) {
    if(clientPurpose.equals("login")) {
        AtomicInteger flag = new AtomicInteger();
        clientHandlers.forEach(clientHandler -> {
            if (clientHandler.clientUsername.equals(clientUsername) && clientHandler.clientPass.equals(clientPass)) {
                flag.getAndIncrement();
            }
        }); if(flag.get() > 0) {
            broadcastToSelf("SERVER: logged in successfully");
        } else {
            broadcastToSelf("No such user");
        }
    } else {
        clientHandlers.add(this);
    }
    }
    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler: clientHandlers) {
            try {
                if(!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void broadcastToSelf(String messageToSend) {
        for(ClientHandler clientHandler: clientHandlers) {
            try {
                if(clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER:" + clientUsername + " has left the chat");
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if(bufferedReader != null) {
                bufferedReader.close();
            }
            if(bufferedWriter != null) {
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
