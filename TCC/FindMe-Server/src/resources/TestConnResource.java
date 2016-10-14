package resources;

import javax.ws.rs.GET; 
import javax.ws.rs.Path; 
import javax.ws.rs.Produces;  

@Path("/testconn") 
public class TestConnResource {
	@GET @Produces("text/plain") 
	public String showConnectionOk() { 
		return "Conexao OK!"; 
	} 
}