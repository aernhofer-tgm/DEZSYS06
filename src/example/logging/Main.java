package example.logging;
import org.apache.log4j.*;

public class Main
{
    private static Logger logger = Logger.getRootLogger();

    public static void main( String[] args )
    {
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
        logger.debug( "Meine Debug-Meldung" );
        logger.info(  "Meine Info-Meldung"  );
        logger.warn(  "Meine Warn-Meldung"  );
        logger.error( "Meine Error-Meldung" );
        logger.fatal( "Meine Fatal-Meldung" );
    }
}