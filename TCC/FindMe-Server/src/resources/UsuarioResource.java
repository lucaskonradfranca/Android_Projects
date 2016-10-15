package resources;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET; 
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
	 * GET: URL: http://localhost:8080/FindMe-Server/usuario/update?matricula=113001625&email=lalala@gmail.com&nome=Lucas Konrad Fran�a&first_login=S&senha=123&data_nascimento=29091992
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
			                      @DefaultValue(" ") @QueryParam("data_nascimento") String data_nascimento,
			                      @DefaultValue("-1") @QueryParam("nivel_privacidade") int nivel_privacidade) { 
		Usuario user = new Usuario();
		RetornoRest ret = new RetornoRest();
		
		if (! matricula.equals(" ") ){
			user.setMatricula(matricula);
		}else{
			user.setMatricula(null);
		}
		if (! nome.equals(" ")){
			user.setNome(nome);
		}else{
			user.setNome(null);
		}
		if (! email.equals(" ")){
			user.setEmail(email);
		}else{
			user.setEmail(null);
		}
		if (! senha.equals(" ")){
			user.setSenha(senha);
		}else{
			user.setSenha(null);
		}
		if (! first_login.equals(" ")){
			user.setFirst_login(first_login);
		}else{
			user.setFirst_login(null);
		}
		if (! data_nascimento.equals(" ")){
			user.setData_nascimento(data_nascimento);
		}else{
			user.setData_nascimento(null);
		}
		if (nivel_privacidade >= 0){
			user.setNivel_privacidade(nivel_privacidade);
		}else{
			user.setNivel_privacidade(-1);
		}	
		if (user.atualizar()){
			ret.setMsg("Atualizado com sucesso.");
			ret.setStatus(true);
			user = user.getUsuario(matricula);
			ret.setObjeto(user);
		}else{
			ret.setMsg("Erro ao atualizar os dados do usu�rio");
			ret.setStatus(false);
			ret.setObjeto(null);
		}
		return ret;
	} 
	
}
