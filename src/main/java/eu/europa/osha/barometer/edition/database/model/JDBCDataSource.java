package eu.europa.osha.barometer.edition.database.model;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/JDBCDataSource")
public class JDBCDataSource extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOGGER = LogManager.getLogger(JDBCDataSource.class);

    /**
     * Function to create a connection to a specified database
     * @return Connection the connection to the database 
     * @throws SQLException 
     * @throws ClassNotFoundException 
     * @throws NamingException
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException, NamingException
    {

        Context ctx = new InitialContext();
        DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/osha_dvt");
        Connection con = ds.getConnection();
        LOGGER.info("Connection established to database.");
        return con;
    }

    /**
     * Function to close the connection to the database
     * @param pCon Connection the connection to the database
     */
    public static void closeConnection(Connection pCon)
    {
        try
        {
            pCon.close();
            LOGGER.info("Connection to database closed.");
        }
        catch (SQLException e)
        {
        	LOGGER.error("An error has occurred while closing database connection: "+e.getMessage());
            e.printStackTrace();
        }
    }
}
