//IO.java
import java.io.*;

public class IO {
    /*
    Name: ReadFile
    Purpose: Reads the contents of a file
    Author: Samuel McManus
    Date: October 4, 2020
     */
    static String ReadFile(String Filename){
        try {
            String FileLine;
            StringBuilder FileText = new StringBuilder();
            File MyFile = new File(Filename);
            BufferedReader FileReader = new BufferedReader(new FileReader(MyFile));
            while((FileLine = FileReader.readLine()) != null){
                FileText.append(FileLine + "\n");
            }
            FileReader.close();
            return FileText.toString();
        } catch (IOException e) {
            return "";
        }
    }
}