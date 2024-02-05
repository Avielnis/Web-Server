import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyLogger {
    public static final Logger logger = Logger.getLogger(TCPServer.class.getName());

    static {
        try {

            removeExsitingHandlaers();
            FileHandler fileHandler = new FileHandler("ServerLogs.log", true); // The true parameter appends to the existing file, if it exists.
            fileHandler.setFormatter(new CustomLogFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);

            logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeExsitingHandlaers() {
        Logger rootLogger = Logger.getLogger("");
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }
    }

    static class CustomLogFormatter extends Formatter {
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        @Override
        public String format(LogRecord record) {
            StringBuilder builder = new StringBuilder();
            builder.append("[").append(record.getLevel()).append(": ");
            builder.append(dateFormat.format(new Date(record.getMillis()))).append("] ");
            builder.append(record.getMessage()).append("\n");
            return builder.toString();
        }
    }
}
