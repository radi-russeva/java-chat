import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println("A new client successfully registered");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler); //clientHandler implements runnable
                thread.start(); //starting the thread
            }
        }
        catch (IOException e) {

        }
    }
    public void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println("Server has started, waiting for clients");
        ServerSocket serverSocket = new ServerSocket(1234); //passing on port number
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
