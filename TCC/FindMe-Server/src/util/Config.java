package util;

public class Config {
    //private static String databaseUrl = "jdbc:h2:tcp://localhost/~/test;USER=sa";//"jdbc:h2:mem:account";
	private static String databaseUrl = "jdbc:sqlserver://Lucas\\SQLEXPRESS;databaseName=findme;user=findme;password=findme;";
    
    public static String getDatabaseUrl(){
        return databaseUrl;
    }
}
