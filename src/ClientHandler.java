import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
public class ClientHandler  implements Runnable{
    public static ArrayList<ClientHandler> clientHandelers=new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName=bufferedReader.readLine();
            synchronized (clientHandelers) {
                clientHandelers.add(this);
            }
            broadcastMessage("SERVER: "+clientUserName+" has entered the chat!");
        }catch (IOException e){
            //e.printStackTrace();
            closeEveryThinng(socket,bufferedReader,bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()){
            try {
                messageFromClient=bufferedReader.readLine();
                if (messageFromClient != null) {
                    broadcastMessage(messageFromClient);
                }
            } catch (IOException e) {
                closeEveryThinng(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend){
        synchronized (clientHandelers) {
            for (ClientHandler clientHandler : clientHandelers) {
                try {
                    if (!clientHandler.clientUserName.equals(clientUserName)) {
                        clientHandler.bufferedWriter.write(messageToSend);
                        clientHandler.bufferedWriter.newLine();
                        clientHandler.bufferedWriter.flush();
                    }
                } catch (IOException e) {
                    closeEveryThinng(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }
    public void removeClientHandeler(){
        synchronized (clientHandelers) {
            clientHandelers.remove(this);
            broadcastMessage("SERVER" + clientUserName + "has left the chat");
        }
    }
    public void closeEveryThinng(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        removeClientHandeler();
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
}
