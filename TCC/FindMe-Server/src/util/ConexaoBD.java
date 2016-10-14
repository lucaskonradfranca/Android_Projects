package util;

//Classes mecessarias para uso do Banco de dados
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBD {

	public static String msgErro;
	public static String url;
	public static String driverName;
	public static String username;
	public static String password;
	
	public static java.sql.Connection getConexaoSQL(){

	    Connection connection = null; 
	    try { 
	        // Carrega o driver JDBC 
	        driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; //  JDBC driver
	        url = "jdbc:sqlserver://Lucas\\SQLEXPRESS;databaseName=findme;";
	        username = "findme";
	        password = "findme";
	        
	        Class.forName(driverName); // Create a conexao  com Banco de dados 
	        
	        connection = DriverManager.getConnection(url, username, password);
	    } catch (ClassNotFoundException e) {
	    	// Não pode encontrar o driver para  Conectar
	    	connection = null;
	    	msgErro = "Não foi possível conectar ao BD. Driver não encontrado. \n" + e.getStackTrace();
	    	e.printStackTrace();
            } catch (SQLException e) { 
                // Não pode efetuar a conexao com o banco
            	connection = null;
		    	msgErro = "Não foi possível efetuar a conexão ao BD. \n" + e.getStackTrace();
		    	e.printStackTrace();
        }
        return connection; 
    }
	
	/*
	 * Desconecta do banco
	 * */
	public static boolean disconnect(Connection con) {
        boolean disConnected = false;
       
        try {
            Class.forName(driverName).newInstance();
            con = DriverManager.getConnection(url,username, password);
            con.close();
            disConnected = true;
        } catch( SQLException e ) {
            System.out.println(e.getMessage());
            disConnected = false;
        } catch ( ClassNotFoundException e ) {
            System.out.println(e.getMessage());
            disConnected = false;
        } catch ( InstantiationException e ) {
            System.out.println(e.getMessage());
            disConnected = false;
        } catch ( IllegalAccessException e ) {
            System.out.println(e.getMessage());
            disConnected = false;
        }

        return disConnected;
	}
	
	/*
	 * Executa uma consulta SQL, e retorna um ResultSet
	 * */
	public static ResultSet consultar( Connection con , String query) {
		ResultSet rs = null;
		Statement st;
		
		try{
			st = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = st.executeQuery(query);

			return rs;
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		return rs;
	}
	
	
	/*
	 * Insere/Apaga/Atualiza um novo registro no banco.
	 * */
	public static int execute( Connection con, String query ) {
		Statement st;
		int result = -1;

		try {
			st = con.createStatement();
			result = st.executeUpdate(query);
		} catch ( SQLException e ) {
			e.printStackTrace();
		}

		return result;
	}
	
}


