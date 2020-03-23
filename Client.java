import java.io.*;
import java.net.Socket;

public class Client {

    private static int port=8080;
    private static Socket socket;


    public static void sendIntegers(BufferedWriter writer, BufferedReader reader) throws IOException {
        for (int i=0;i<10;i++) {
            writer.write("integer\n");
            writer.write(i+"\n");
            writer.flush();
            System.out.println("sent integer: "+i);
            String input= reader.readLine();
            System.out.println("received integer: "+input);
        }
    }

    public static void putFile(BufferedWriter writer, BufferedReader reader, String path, String filename) throws IOException {
        try {
            File file_e=new File(path+filename);
            BufferedInputStream file=new BufferedInputStream(new FileInputStream(file_e));
            writer.write("put\n");
            write.flush();
            writer.write(filename+"\n");
            writer.flush();
            int size= (int) file_e.length();
            System.out.println("size: "+size);
            writer.write(size+"\n");
            writer.flush();
            byte[] arr=new byte[size];
            System.out.println(arr.length);
            file.read(arr, 0, arr.length);
            socket.getOutputStream().write(arr, 0, arr.length);
            socket.getOutputStream().flush();
            file.close();
            System.out.println("File sent");
        }catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public static void getFile(BufferedWriter writer, BufferedReader reader, String path, String filename, int size) throws IOException {
        BufferedOutputStream file=new BufferedOutputStream(new FileOutputStream(path+filename));
        byte[] arr =new byte[size];
        socket.getInputStream().read(arr, 0, arr.length);
        file.write(arr, 0, arr.length);
        file.flush();
        file.close();
        System.out.println("File received");
    }

    public static void main(String[] args) throws IOException {
        socket=new Socket("localhost", port);
        System.out.println("Connected to server");
        BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        while (true) {
            System.out.println();
            System.out.println("Select option");
            System.out.println("1. Send Integer");
            System.out.println("2. Send File");
            System.out.println("3. Request File");
            System.out.println("4. exit");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
            String inp=bufferedReader.readLine().trim();
            switch (inp.toLowerCase()) {
                case "1":
                    sendIntegers(writer, reader);
                    break;
                case "2":
                    System.out.println("Enter filename: ");
                    String filename=bufferedReader.readLine();
                    putFile(writer, reader, "", filename);
                    break;
                case "3":
                    System.out.println("Enter Filename:");
                    filename=bufferedReader.readLine();
                    writer.write("get\n");
                    writer.write(filename+"\n");
                    writer.flush();
                    String status=reader.readLine();
                    if (status.trim().equals("file not found")) {
                        System.out.println("File does not exist");
                    }else {
                        int size=Integer.parseInt(reader.readLine());
                        getFile(writer, reader, "", filename, size);
                    }
                    break;
                case "4":
                    writer.write("disconnect\n");
                    writer.flush();
                    reader.close();
                    writer.close();
                    socket.close();
                    System.exit(0);
                    break;
                default: System.out.println("Invalid Input");
            }
        }
    }
}
