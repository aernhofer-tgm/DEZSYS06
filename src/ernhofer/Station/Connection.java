package ernhofer.Station;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by andie on 03.02.2016.
 */
public interface Connection {
    public void connect();
    public ResultSet execute(String query) throws SQLException;
    public String print(ResultSet rs);
    public void close();
}
