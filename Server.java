import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static int port = 8080;
    private static Socket socket;

    public static void putFile(BufferedWriter writer, BufferedReader reader, String path, String filename, int size) throws IOException 
    {
        BufferedOutputStream file=new BufferedOutputStream(new FileOutputStream(path+filename));
        byte[] arr = new byte[size];
        socket.getInputStream().read(arr, 0, arr.length);
        file.write(arr, 0, arr.length);
        file.flush();
        file.close();
        System.out.println("File received");
    }

    public static void getFile(BufferedWriter writer, BufferedReader reader, String path, String filename) throws IOException 
    {
        try 
        {
            File file_e=new File(path+filename);
            BufferedInputStream file=new BufferedInputStream(new FileInputStream(file_e));
            writer.write("file found\n");
            writer.flush();
            int size= (int) file_e.length();
            writer.write(size+"\n");
            writer.flush();
            byte[] arr=new byte[size];
            file.read(arr, 0, arr.length);
            socket.getOutputStream().write(arr, 0, arr.length);
            socket.getOutputStream().flush();
            file.close();
            System.out.println("File sent");
        } catch (FileNotFoundException e) 
        {
            System.out.println("File not found");
            writer.write("file not found\n");
            writer.flush();
        }
    }

    public static void main(String[] args) throws IOException 
    {
        ServerSocket server=new ServerSocket(port);
        System.out.println("Server started at port: "+port);
        while (true) 
        {
            socket=server.accept();
            BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String input;
            while ((input=reader.readLine())!=null) 
            {
                if (input.trim().toLowerCase().equals("disconnect")) 
                {  //command
                    System.out.println("disconnect");
                    //close the connection
                    writer.close();
                    reader.close();
                    socket.close();
                    break;
                }
                else if (input.trim().equals("integer")) 
                { //command
                    //returns incremented integer
                    System.out.println("integer");
                    input=reader.readLine();
                    int i=Integer.parseInt(input)+1;
                    writer.write(i+"\n");
                    writer.flush();
                }
                else if (input.trim().equals("get")) 
                { //command
                    //send the file
                    System.out.println("get");
                    String filename= reader.readLine();    //receive file name
                    getFile(writer, reader,"", filename);
                }
                else if (input.trim().equals("put")) 
                { //command
                    System.out.println("put");
                    String filename= reader.readLine();    //receive file name
                    int size=Integer.parseInt(reader.readLine());
                    putFile(writer, reader,"", filename, size);
                }
                else {
                    System.out.println("Invalid command");
                }
            }
        }
    }
}
