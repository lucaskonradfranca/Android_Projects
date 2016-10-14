package resources;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET; 
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import models.Usuario;
import util.RetornoRest;  

@Path("/login") 
public class LoginResource {

	/*
	 * GET: URL: http://localhost:8080/FindMe-Server/login?user=113001625&pass=22021994
	 * Definição do método:
	 * 	@GET 
    	@Produces("text/plain") 
	    public String realizarLogin(@DefaultValue(" ") @QueryParam("user") String usuario,
		    	                    @DefaultValue(" ") @QueryParam("pass") String senha) { 
	 * */
	
	//@Path("{userpass}")
	@GET 
	@Produces(MediaType.APPLICATION_JSON) 
	public RetornoRest realizarLogin(@DefaultValue(" ") @QueryParam("user") String usuario,
			                    @DefaultValue(" ") @QueryParam("pass") String senha) { 
		Usuario user = new Usuario();
		RetornoRest ret = new RetornoRest();
		
		if (user.logar(usuario, senha)){
			ret.setMsg("Realizou login");
			ret.setStatus(true);
			ret.setObjeto(user);
		}else{
			ret.setMsg(user.getMsg_erro());
			ret.setStatus(false);
			ret.setObjeto(null);
		}
		return ret;
	} 
	
}
