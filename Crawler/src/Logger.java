import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;


public class Logger {

    private PrintWriter pw;
    private String logFolder = ".\\logs\\";

    public Logger(String nome) {
        String timestamp = new Timestamp((new java.util.Date()).getTime()).toString();

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(logFolder + "log_"+nome+"_"+System.currentTimeMillis()+".txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String l){
        String time = new Timestamp(new java.util.Date().getTime()).toString();
        pw.write("[ " + time + " ] - " + l + "\n");
        pw.flush();
    }


}
