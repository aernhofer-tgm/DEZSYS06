package example.logging;
import org.apache.log4j.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main
{
    private static final Logger logger = Logger.getRootLogger();

    public static void main( String[] args ) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("logs/log4j.properties"));
            PropertyConfigurator.configure(props);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            PatternLayout layout = new PatternLayout( "<%d{yyyy-MM-dd HH:mm:ss}> %-5p: [%t]: %m%n" );
            ConsoleAppender consoleAppender = new ConsoleAppender( layout );
            logger.addAppender( consoleAppender );
            //Layout, File, keep old data
            FileAppender fileAppender = new FileAppender( layout, "logs/MeineLogDatei.log", false );
            logger.addAppender( fileAppender );
            // ALL | DEBUG | INFO | WARN | ERROR | FATAL | OFF:
            logger.setLevel( Level.INFO );
        } catch( Exception ex ) {
            System.out.println( ex );
        }
        */
        logger.trace("Entering application.");
        logger.debug( "Meine Debug-Meldung" );
        logger.info(  "Meine Info-Meldung"  );
        logger.warn(  "Meine Warn-Meldung"  );
        logger.error( "Meine Error-Meldung" );
        logger.fatal( "Meine Fatal-Meldung" );
    }
}