//package Examples;
//
//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.io.IOException;
//
//public class ReadFile {
//    public static void main(String[] args) {
//        ServerConfig config = ServerConfig.getInstance();
//        String filePath = config.getRoot() + config.getDefaultPage();
//
//        try {
//            FileReader fileReader = new FileReader(filePath);
//            BufferedReader bufferedReader = new BufferedReader(fileReader);
//
//            String line;
//            while ((line = bufferedReader.readLine()) != null) {
//                System.out.println(line); // Print each line to the console
//            }
//
//            bufferedReader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
