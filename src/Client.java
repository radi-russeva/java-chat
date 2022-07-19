import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;
    private String userPass;
    private String userPurpose;
//    private DataInputStream dataInputStream;
//    private DataOutputStream dataOutputStream;

    public Client(Socket socket, String userName, String userPass, String userPurpose) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = userName;
            this.userPass = userPass;
            this.userPurpose = userPurpose;

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage() {
        try {
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(userPass);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.write(userPurpose);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(userName + ": " +  messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while(socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
//    private void sendFile(String path) {
//        try {
//            int bytes = 0;
//            File file = new File(path);
//            FileInputStream fileInputStream = new FileInputStream(file);
//
//            // send file size
//            this.dataOutputStream.writeLong(file.length());
//            // break file into chunks
//            byte[] buffer = new byte[4 * 1024];
//            while ((bytes = fileInputStream.read(buffer)) != -1) {
//                dataOutputStream.write(buffer, 0, bytes);
//                dataOutputStream.flush();
//            }
//            fileInputStream.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username please: ");
        String userName = scanner.nextLine();
        System.out.println("Enter your password please: ");
        String userPass = scanner.nextLine();
        System.out.println("Enter your purpose, please: ");
        String userPurpose = scanner.nextLine();
        System.out.println("Success! Now write some messages: ");
//        String filePath = scanner.nextLine();

        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, userName, userPass, userPurpose);

        client.listenForMessage();
        client.sendMessage();
//        client.sendFile(filePath);
    }
}
