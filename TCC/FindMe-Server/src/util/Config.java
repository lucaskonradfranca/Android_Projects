package util;

public class Config {
    //private static String databaseUrl = "jdbc:h2:tcp://localhost/~/test;USER=sa";//"jdbc:h2:mem:account";
	//private static String databaseUrl = "jdbc:sqlserver://Lucas\\SQLEXPRESS;databaseName=findme;user=findme;password=findme;";
	private static String databaseUrl = "jdbc:sqlserver://FindMe;databaseName=findme;user=findme;password=lucas.franca@123;";
    
    public static String getDatabaseUrl(){
        return databaseUrl;
    }
}
