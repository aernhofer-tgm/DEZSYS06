package ernhofer.Station;

import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andie on 03.02.2016.
 */
public interface Connection {
    public void connect() throws SQLException;
    public ResultSet execute(String query) throws SQLException, SocketTimeoutException;
    public String print(ResultSet rs);
    public void close();
    public String getAdress();
}
