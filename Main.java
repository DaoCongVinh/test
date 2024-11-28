import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static List<Student> students = new ArrayList<>();
    private static Map<String, String> accounts = new HashMap<>();

    // Khởi tạo dữ liệu mẫu
    static{
        students.add(new Student(01, "NGUYEN VAN AN", 8.5));
        students.add(new Student(02, "TRAN THI PHUONG TUYEN", 9.0));
        students.add(new Student(03, "NGUYEN THI HONG NHU", 9.2));
        students.add(new Student(01, "NGUYEN PHUONG NAM", 8.0));
        students.add(new Student(02, "TRAN HOANG PHONG", 7.0));
        students.add(new Student(03, "NGUYEN THI NGOC YEN", 5.0));

        // Tài khoản đăng nhập mẫu
        accounts.put("admin", "123");
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private boolean check = false;
        private String currentUser = null;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                output.println("CHAO MUNG DEN VOI SERVER FINDBY SINH VIEN");

                String inputLine;
                while ((inputLine = input.readLine()) != null) {
                    String[] parts = inputLine.split("\\s+", 2);
                    String command = parts[0].toUpperCase();
                    String param = parts.length > 1 ? parts[1] : "";

                    if (!check) {
                        handleAuthCommands(command, param);
                    } else {
                        handleQueryCommands(command, param);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleAuthCommands(String command, String param) throws IOException {
            switch (command) {
                case "U":
                    if (accounts.containsKey(param)) {
                        output.println("User ok, need password");
                        currentUser = param;
                    } else {
                        output.println("KHONG TIM THAY USER");
                    }
                    break;
                case "P":
                    if (currentUser != null &&
                            accounts.get(currentUser).equals(param)) {
                        check = true;
                        output.println("BAN DA DANG NHAP THANH CONG");
                    } else {
                        output.println("TK HOAC MK KHONG CHINH XAC VUI LONG NHAP LAI");
                        currentUser = null;
                    }
                    break;
                case "QUIT":
                    output.println("NGAT KET NOI");
                    socket.close();
                    break;
                default:
                    output.println("LENH KHONG TON TAI");
            }
        }

        private void handleQueryCommands(String command, String param) {
            switch (command) {
                case "FBD":
                    findStudentById(param);
                    break;
                case "FBN":
                    findStudentByName(param);
                    break;
                case "QUIT":
                    try {
                        output.println("221 Goodbye");
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    output.println("LENH KHONG TON TAI");
            }
        }

        private void findStudentById(String id) {
            boolean found = false;
            try {
                int studentId = Integer.parseInt(id);
                for (Student student : students) {
                    if (student.mssv == studentId) {
                        output.println(student);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    output.println("KHONG TIM THAY MSSV");
                }
            } catch (NumberFormatException e) {
                output.println("MA SINH VIEN KHONG HOP LE");
            }
        }

        private void findStudentByName(String name) {
            boolean found = false;
            for (Student student : students) {
                if (student.name.toLowerCase().contains(name.toLowerCase())) {
                    output.println(student);
                    found = true;
                }
            }
            if (!found) {
                output.println("KHONG TIM THAY SINH VIEN");
            }
        }
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(54321)) {
            System.out.println("Server đang chạy trên cổng 54321...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}