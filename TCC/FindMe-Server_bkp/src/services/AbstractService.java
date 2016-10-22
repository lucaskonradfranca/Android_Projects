package services;

import util.Config;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractService {
    
    protected ConnectionSource connectionSource;
    
    public AbstractService(){
        try {
            String databaseUrl = Config.getDatabaseUrl();
            connectionSource = new JdbcConnectionSource(databaseUrl);
        } catch (SQLException ex) {
            Logger.getLogger(AbstractService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
