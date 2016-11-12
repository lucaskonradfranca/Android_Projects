package resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import models.APResource;
import models.PredictionRequest;
import models.Room;
import models.Usuario;
import services.PredictionService;
import util.RetornoRest;  

@Path("/usuario") 
public class UsuarioResource {

	/*
	 * GET URL: http://10.8.143.217:8080/FindMe-Server/usuario/getUsuarios?matricula=113001625&filtro=Joao
	 * */
	
	
	@Path("/getUsuarios")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest getUsuarios(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			@DefaultValue(" ") @QueryParam("filtro") String filtro){
		System.out.println("GetUsuarios");
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		List<Usuario> usuarios = user.getUsuarios(filtro);
		
		if (usuarios == null){
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}else{
			ret.setStatus(true);
		}
	
		ret.setObjeto(Response.ok(usuarios).build());
		return ret;
	}
	
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
			ret.setMsg("Erro ao atualizar os dados do usuário");
			ret.setStatus(false);
			ret.setObjeto(null);
		}
		return ret;
	} 
	
	/*
	 * Atualiza no servidor os AP's disponíveis para o usuário.
	 * 
	 * Parâmetro GET: 
	 ApList - (json em formato string)
	 [
       {"bssid": "1", "ssid": "lalala", "rssi":"-20"},
       {"bssid": "2", "ssid": "lalala2", "rssi":"-50"}
     ] 
     Matrícula - 11111111
     
     URL POST: http://localhost:8080/FindMe-Server/usuario/myAccessPoints

	 * 
	 * */
	
	@GET
	@Path("/myAccessPoints")
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest myAccessPoints(@QueryParam("apList") String jsonApList, 
									  @QueryParam("matricula") String matricula,
									  @QueryParam("usuarioOrigem") String usuarioOrigem){

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(jsonApList);
		JsonArray apListJson = element.getAsJsonArray();
		Usuario user = new Usuario();
		RetornoRest ret = new RetornoRest();
		
		System.out.println("Atualizando dados dos APS.");
		
		user.setMatricula(matricula);
		
		if (user.updateAps(apListJson, usuarioOrigem)){
			ret.setStatus(true);
		}else{
			ret.setStatus(false);
		}
		
		return ret;
	}
	
	@GET
	@Path("/getLocation")
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest getLocation(@QueryParam("matricula") String matricula, @QueryParam("usuarioOrigem") String usuarioOrigem){
		/*matricula = matricula do usuário que será localizado
		 * usuarioOrigem = matrícula do usuário que solicitou a localização*/
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		if (!user.locate(usuarioOrigem)){
			ret.setStatus(false);
		}else{
			ret.setStatus(true);
		}
		ret.setMsg(user.getMsg_erro());
		
		return ret;
	}
	
	/*
	 * Busca a própria localização.
	 * 
	 * Parâmetro POST: 
	 [
       {"bssid": "1", "ssid": "lalala", "rssi":"-20"},
       {"bssid": "2", "ssid": "lalala2", "rssi":"-50"}
     ]
     
     URL POST: http://localhost:8080/FindMe-Server/usuario/getMyLocation

	 * 
	 * */
	/*
	@POST
	@Path("/getMyLocation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest getMyLocation(List<APResource> apList){*/
	@GET
	@Path("/getMyLocation")
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest getMyLocation(@QueryParam("apList") String jsonApList /*List<APResource> apList*/){
		System.out.println(jsonApList);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(jsonApList);
		JsonArray apListJson = element.getAsJsonArray();
		RetornoRest ret = new RetornoRest();
		PredictionRequest request = new PredictionRequest();
		for(Object js : apListJson){
			Gson gson = new Gson();
			APResource ap = gson.fromJson(js.toString(), APResource.class);
			request.addAccessPoint(ap.getBSSID(), ap.getRSSI());
		}
		PredictionService predServ = PredictionService.getInstance();
		Room room = predServ.predict(request);
		
		ret.setMsg(room.getName());
		ret.setStatus(true);
		//ret.setObjeto(room);
		return ret;
	}
	
	/*
	 * GET: URL: http://localhost:8080/FindMe-Server/usuario/update?matricula=113001625&email=lalala@gmail.com&nome=Lucas Konrad França&first_login=S&senha=123&data_nascimento=29091992
	 * 
	 * */
	
	@Path("/updateToken")
	@GET 
	@Produces(MediaType.APPLICATION_JSON) 
	public RetornoRest updateToken(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			                      @DefaultValue(" ") @QueryParam("token") String token) { 
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		
		if(user.updateToken(token)){
			ret.setStatus(true);
			ret.setMsg("Token atualizado com sucesso.");
			ret.setObjeto(null);					
		}else{
			ret.setStatus(false);
			ret.setMsg("Ocorreram erros ao atualizar o token do usuário.");
			ret.setObjeto(null);
		}
		
		return ret;
	} 
	
	/*
	 * GET URL: http://10.8.143.217:8080/FindMe-Server/usuario/adicionar?matricula=113001625&matriculaAmigo=113001626
	 * */
	
	
	@Path("/adicionar")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest adicionar(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			@DefaultValue(" ") @QueryParam("matriculaAmigo") String matriculaAmigo){
		System.out.println("adicionar");
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		
		if (user.adicionar(matriculaAmigo)){
			ret.setStatus(true);
		}else{
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}
	
		return ret;
	}
	
	/*
	 * GET URL: http://10.8.143.217:8080/FindMe-Server/usuario/adicionar?matricula=113001625&matriculaAmigo=113001626
	 * */
	
	
	@Path("/getSolicitacaoAmizade")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest getSolicitacaoAmizade(@DefaultValue(" ") @QueryParam("matricula") String matricula){
		System.out.println("getSolicitacaoAmizade");
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		List<Usuario> usuarios = user.getSolicitacoesAmizade(matricula);
		
		if (usuarios == null){
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}else{
			ret.setStatus(true);
		}
	
		ret.setObjeto(Response.ok(usuarios).build());
		return ret;
	}
	
	@Path("/atualizaSolicitacaoAmizade")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest atualizaSolicitacaoAmizade(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			@DefaultValue(" ") @QueryParam("matriculaAmigo") String matriculaAmigo,
			@DefaultValue(" ") @QueryParam("aceito") String aceito){
		System.out.println("atualizaSolicitacaoAmizade");
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		
		if (user.atualizaSolicitacaoAmizade(matriculaAmigo, aceito)){
			ret.setStatus(true);
		}else{
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}
		return ret;
	}
	
	@Path("/bloquear")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest bloquear(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			@DefaultValue(" ") @QueryParam("matriculaAmigo") String matriculaAmigo,
			@DefaultValue(" ") @QueryParam("block") String block){
		System.out.println("bloquear");
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		
		if (user.bloquear(matriculaAmigo, block)){
			ret.setStatus(true);
		}else{
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}
		return ret;
	}
	
	@Path("/excluir")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RetornoRest excluir(@DefaultValue(" ") @QueryParam("matricula") String matricula,
			@DefaultValue(" ") @QueryParam("matriculaAmigo") String matriculaAmigo){
		System.out.println("excluir");
		RetornoRest ret = new RetornoRest();
		Usuario user = new Usuario();
		user.setMatricula(matricula);
		
		if (user.excluir(matriculaAmigo)){
			ret.setStatus(true);
		}else{
			ret.setStatus(false);
			ret.setMsg(user.getMsg_erro());
		}
		return ret;
	}
	
}
