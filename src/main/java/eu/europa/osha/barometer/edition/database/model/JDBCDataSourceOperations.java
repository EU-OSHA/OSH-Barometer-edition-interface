package eu.europa.osha.barometer.edition.database.model;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class JDBCDataSourceOperations
{

    // Database Queries
    private static ResourceBundle dbStatements;

    public static ArrayList<HashMap<String, String>> launchSelect(String pQuery, String[] pParams, String url) throws Exception
    {
        dbStatements = ResourceBundle.getBundle(url);
        String select = dbStatements.getString(pQuery);
        Connection con = null;

        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        try {
            con = JDBCDataSource.getConnection();
            if (con != null)
            {

                PreparedStatement statement = con.prepareStatement(select);

                for (int i = 1; i <= pParams.length; i++)
                {
                    if(select.contains("LIMIT") && i == pParams.length){
                        try{
                            if(pParams[i-1] != null){
                                if(Integer.parseInt(pParams[i-1]) >= 0){
                                    statement.setInt(i, Integer.parseInt(pParams[i-1]));
                                }else{
                                    statement.setString(i, pParams[i - 1]);
                                }
                            }else{
                                statement.setString(i, pParams[i - 1]);
                            }
                        }catch(NumberFormatException e){
                            System.out.println("Ha ocurrido un error");
                        }
                    }else
                        statement.setString(i, pParams[i - 1]);
                }

                ResultSet rs = statement.executeQuery();
                //System.out.println("QUERY: "+statement);

                // Get Number of columns of ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnNumber = rsmd.getColumnCount();

                // For each row, save the data retrieved by the select
                while (rs.next())
                {
                    HashMap<String, String> row = new HashMap<String, String>();
                    for (int i = 1; i <= columnNumber; i++)
                    {
                        // Add the name of the column and its value
                        //row.put(rsmd.getColumnName(i) ,rs.getString(i));
                        row.put(rsmd.getColumnLabel(i) ,rs.getString(i));
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }finally {
            try
            {
                JDBCDataSource.closeConnection(con);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }

    // @params
    // pQuery: Query to be launched. it can be a SELECT or an UPDATE. The gaps for the params will be replaced with ?
    // pParams: Array of Strings with the values for the gaps. The params will be in the correct order. It will have as many elements as gaps are in the Query
    public static void launchQuery (String pQuery, String[] pParams, String url) {
        dbStatements = ResourceBundle.getBundle(url);
        String query = dbStatements.getString(pQuery);
        Connection con = null;
        try {
            // Get the connection for the JDBC
            con = JDBCDataSource.getConnection();
            if (con != null) {
                PreparedStatement statement = con.prepareStatement(query);

                // For each element on pParams, add it to the query
                for (int i = 1; i <= pParams.length; i++) {
                    statement.setString(i, pParams[i - 1]);
                }

                // Launch the query
                statement.executeUpdate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // Close the connection
                JDBCDataSource.closeConnection(con);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void launchUpdate(String query){
        Connection con = null;
        try {
            // Get the connection for the JDBC
            con = JDBCDataSource.getConnection();
            if (con != null) {
                PreparedStatement statement = con.prepareStatement(query);
                // Launch the query
                statement.executeUpdate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // Close the connection
                JDBCDataSource.closeConnection(con);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<HashMap<String, String>> launchPreparedQuery(String query){
        ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
        Connection con = null;
        try {
            con = JDBCDataSource.getConnection();
            if (con != null)
            {

                PreparedStatement statement = con.prepareStatement(query);

                ResultSet rs = statement.executeQuery();

                // Get Number of columns of ResultSet
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnNumber = rsmd.getColumnCount();

                // For each row, save the data retrieved by the select
                while (rs.next())
                {
                    HashMap<String, String> row = new HashMap<String, String>();
                    for (int i = 1; i <= columnNumber; i++)
                    {
                        // Add the name of the column and its value
                        //row.put(rsmd.getColumnName(i) ,rs.getString(i));
                        row.put(rsmd.getColumnLabel(i) ,rs.getString(i));
                    }
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }finally {
            try
            {
                JDBCDataSource.closeConnection(con);
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return result;
    }
}
