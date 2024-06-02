import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String Username;

    public Client(Socket socket, String username) {
        try {
            this.socket=socket;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.Username=username;
        } catch (IOException e) {
            closeEveryThing(socket,bufferedReader,bufferedWriter);
            e.printStackTrace();
        }
    }
    public void sendMessage(){
        try {
            bufferedWriter.write(Username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner=new Scanner(System.in);
            while (socket.isConnected()){
                String messsageToSend=scanner.nextLine();
                bufferedWriter.write(Username+": "+messsageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEveryThing(socket,bufferedReader,bufferedWriter);
            e.printStackTrace();
        }
    }
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        if (msgFromGroupChat != null) {
                            System.out.println(msgFromGroupChat);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        closeEveryThing(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEveryThing(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        try {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        Scanner scanner=new Scanner(System.in);
        System.out.println("enter your user name for the group chat");
        String userName=scanner.nextLine();
        Socket socket=new Socket("localhost",8080);
        Client client=new Client(socket,userName);
        client.listenForMessage();
        client.sendMessage();
    }
}
