package resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET; 
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Usuario;
import util.RetornoRest;  

@Path("/usuario") 
public class UsuarioResource {

	/*
	 * GET URL: http://10.8.143.217:8080/FindMe-Server/usuario/getAmigos?matricula=113001625
	 * */
	
	@Path("/getAmigos")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest GetAll(@DefaultValue(" ") @QueryParam("matricula") String matricula){
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		List<Usuario> usuarios = user.getAmigos(matricula);
		
		if (usuarios == null){
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}else{
			ret.setStatus(true);
		}
	
		ret.setObjeto(Response.ok(usuarios).build());
		return ret;
	}
	
	/*
	 * GET: URL: http://localhost:8080/FindMe-Server/usuario/update?matricula=113001625&email=lalala@gmail.com&nome=Lucas Konrad França&first_login=S&senha=123&data_nascimento=29091992
	 * 
	 * */
	
	@Path("/update")
	@GET 
	@Produces(MediaType.APPLICATION_JSON) 
	public RetornoRest UpdateUser(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			                      @DefaultValue(" ") @QueryParam("nome") String nome,
			                      @DefaultValue(" ") @QueryParam("email") String email,
			                      @DefaultValue(" ") @QueryParam("senha") String senha,
			                      @DefaultValue(" ") @QueryParam("first_login") String first_login,
			                      @DefaultValue(" ") @QueryParam("data_nascimento") String data_nascimento) { 
		Usuario user = new Usuario();
		RetornoRest ret = new RetornoRest();
		
		if (matricula != " "){
			user.setMatricula(matricula);
		}else{
			user.setMatricula(null);
		}
		if (nome != " "){
			user.setNome(nome);
		}else{
			user.setNome(null);
		}
		if (email != " "){
			user.setEmail(email);
		}else{
			user.setEmail(null);
		}
		if (senha != " "){
			user.setSenha(senha);
		}else{
			user.setSenha(null);
		}
		if (first_login != " "){
			user.setFirst_login(first_login);
		}else{
			user.setFirst_login(null);
		}
		if (data_nascimento != " "){
			user.setData_nascimento(data_nascimento);
		}else{
			user.setData_nascimento(null);
		}
			
		if (user.atualizar()){
			ret.setMsg("Atualizado com sucesso.");
			ret.setStatus(true);
			ret.setObjeto(user);
		}else{
			ret.setMsg("Erro ao atualizar os dados do usuário");
			ret.setStatus(false);
			ret.setObjeto(null);
		}
		return ret;
	} 
	
}
