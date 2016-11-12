package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import services.PredictionService;
import util.ConexaoBD;

public class Usuario {
	public Usuario() {
		super();
	}
	public Usuario(String matricula, String nome, String email, String senha, String data_nascimento,
			String first_login, int nivel_privacidade) {
		super();
		this.matricula = matricula;
		this.nome = nome;
		this.email = email;
		this.senha = senha;
		this.data_nascimento = data_nascimento;
		this.first_login = first_login;
		this.nivel_privacidade = nivel_privacidade;
	}


	//Dados do usuário
	private String matricula = "";
	private String nome = "";
	private String email = "";
	private String senha = "";
	private String data_nascimento = "";
	private String first_login = "";
	private int nivel_privacidade = 0;
	
	
	@Override
	public String toString() {
		return "Objeto [matricula=" + matricula.trim() + ", nome=" + nome.trim() + 
				", email="+email.trim()+", data_nascimento="+data_nascimento.trim()+
				", first_login="+first_login.trim()+", nivel_privacidade="+nivel_privacidade+"]";
	}
	
	
	//Variáveis de controle interno
	private String msg_erro;
	
	public boolean logar(String matriculaUser, String senhaUser){
		boolean status = false;
		
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
		}else{
			ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario WHERE matricula = '"+matriculaUser+"'; ");
			try{
				if (rs.first()){
					if(rs.getString("first_login").equals("S")){
						if(rs.getString("data_nascimento").trim().equals(senhaUser)){
							status = true;
						}else{
							status = false;
							setMsg_erro("Usuário não autenticado.");
						}
					}else{
						if(rs.getString("senha").trim().equals(senhaUser)){
							status = true;
						}else{
							status = false;
							setMsg_erro("Usuário não autenticado.");
						}
					}
				}else{
					status = false;
					setMsg_erro("Usuário não autenticado.");
				}
				if (status){
					this.matricula       = rs.getString("matricula");
					this.nome            = rs.getString("nome");
					this.email           = rs.getString("email");
					this.data_nascimento = rs.getString("data_nascimento");
					this.first_login     = rs.getString("first_login");
					this.nivel_privacidade = rs.getInt("nivel_privacidade");
				}
			}catch (SQLException e){
				e.printStackTrace();
				setMsg_erro("Erro ao realizar a autenticação." + e.getMessage());
			}catch (Exception e){
				e.printStackTrace();
				setMsg_erro("Erro ao realizar a autenticação." + e.getMessage());
			}
		}
		
		return status;
	}
	
	public boolean atualizar(){
		boolean status = false;
		
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
		}else{
			String query = "";
			boolean virgula = false;
			
			query = " UPDATE usuario SET ";
			if ((this.nome != null) && (! this.nome.isEmpty()) && (! this.nome.equals(" "))){
				query += " nome = '" + this.nome + "' ";
				virgula = true;
			}else{
				if (virgula){
					query += ", ";
				}
				query+= " nome = nome ";
				virgula = true;
			}
			if ((this.email != null) &&(! this.email.equals("")) && (!this.email.isEmpty())){
				if (virgula){
					query += ", ";
				}
				query += " email = '" + this.email + "' ";
				virgula = true;
			}else{
				if (virgula){
					query += ", ";
				}
				query+= " email = email ";
				virgula = true;
			}
			if ((this.senha != null) &&(! this.senha.equals("")) && (! this.senha.isEmpty())){
				if (virgula){
					query += ", ";
				}
				query += " senha = '" + this.senha + "' ";
				virgula = true;
			}else{
				if (virgula){
					query += ", ";
				}
				query+= " senha = senha ";
				virgula = true;
			}
			if ((this.data_nascimento != null) &&(! this.data_nascimento.equals("")) && (! this.data_nascimento.isEmpty())){
				if (virgula){
					query += ", ";
				}
				query += " data_nascimento = '" + this.data_nascimento + "' ";
				virgula = true;
			}else{
				if (virgula){
					query += ", ";
				}
				query+= " data_nascimento = data_nascimento";
				virgula = true;
			}
			if ((this.first_login != null) &&(! this.first_login.equals("")) && (! this.first_login.isEmpty())){
				if (virgula){
					query += ", ";
				}
				query += " first_login = '" + this.first_login + "' ";
				virgula = true;
			}else{
				if (virgula){
					query += ", ";
				}
				query+= " first_login = first_login ";
				virgula = true;
			}
			if (this.nivel_privacidade >= 0 && this.nivel_privacidade <= 3){
				if (virgula){
					query += ", ";
				}
				query += " nivel_privacidade = '" + this.nivel_privacidade + "' ";
				virgula = true;
			}else{
				if (virgula){
					query += ", ";
				}
				query+= " nivel_privacidade = nivel_privacidade ";
				virgula = true;
			}
			query += " WHERE matricula = '" + this.matricula + "'";
			int result = ConexaoBD.execute(con,query);
			if (result < 0){
				status = false;
			}else{
				status = true;
			}
		}
		
		return status;
	}

	public List<Usuario> getAmigos(String matricula){
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
			usuarios = null;
		}else{
			//ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario WHERE matricula <> '"+matricula+"' ORDER BY nome; ");
			ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario "
					+ " WHERE usuario.matricula IN ( SELECT amigos.matricula_amigo FROM amigos WHERE amigos.matricula = '"+matricula+"') ORDER BY nome; ");
			try{
				boolean achou = false;
				while (rs.next()){
					achou = true;
					usuarios.add(new Usuario(rs.getString("matricula"),
							rs.getString("nome"),
							rs.getString("email"),
							"senha",
							rs.getString("data_nascimento"),
							rs.getString("first_login"),
							rs.getInt("nivel_privacidade")));
				}
				if (!achou){
					setMsg_erro("Nenhum usuário encontrado.");
				}
			}catch (SQLException e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
				usuarios = null;
			}catch (Exception e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
				usuarios = null;
			}
		}
		
		return usuarios;
	}

	public List<Usuario> getUsuarios(String filtro){
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
			usuarios = null;
		}else{
			String query = " SELECT * "
					       + " FROM usuario "
					      + " WHERE matricula <> '" + this.matricula + "' ";
			
			if (! filtro.equals(" ") && ! filtro.isEmpty()){
				query += " AND nome LIKE '%" + filtro + "%' ";
			}
			
			query += " AND matricula NOT IN (SELECT amigos.matricula_amigo FROM amigos WHERE amigos.matricula = '"+this.matricula+"')";
			
			query += " ORDER BY nome; ";
			
			ResultSet rs = ConexaoBD.consultar(con, query);
			try{
				boolean achou = false;
				while (rs.next()){
					achou = true;
					usuarios.add(new Usuario(rs.getString("matricula"),
							rs.getString("nome"),
							rs.getString("email"),
							"senha",
							rs.getString("data_nascimento"),
							rs.getString("first_login"),
							rs.getInt("nivel_privacidade")));
				}
				if (!achou){
					setMsg_erro("Nenhum usuário encontrado.");
				}
			}catch (SQLException e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
				usuarios = null;
			}catch (Exception e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
				usuarios = null;
			}
		}
		
		return usuarios;
	}
	public Usuario getUsuario(String matriculaUser){
		Usuario user = new Usuario();

		Connection con = ConexaoBD.getConexaoSQL();
		ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario WHERE matricula = '"+matriculaUser+"'; ");

		try{
			if (rs.first()){
				user.setMatricula(rs.getString("matricula"));
				user.setNome(rs.getString("nome"));
				user.setEmail(rs.getString("email"));
				user.setData_nascimento(rs.getString("data_nascimento"));
				user.setFirst_login(rs.getString("first_login"));
				user.setNivel_privacidade(rs.getInt("nivel_privacidade"));
				user.setSenha(rs.getString("senha"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		return user;
	}
	
	public boolean updateAps(JsonArray apList, String usuarioOrigem){
		boolean status = true;
		
		if (this.matricula.isEmpty()){
			status = false;
		}else{
			Connection con = ConexaoBD.getConexaoSQL();
			String query = "";
			int result = 0;
			
			query = "DELETE FROM usuario_aps WHERE matricula = '" + this.matricula + "' AND usuario_origem = '"+usuarioOrigem+"'; ";
			result = ConexaoBD.execute(con,query);
			if (result < 0){
				status = false;
			}else{
				query = " INSERT INTO usuario_aps (matricula, bssid, ssid, rssi, sequencia, usuario_origem) VALUES ";
				boolean virgula = false;
				int sequencia = 0;
				for(Object js : apList){
					Gson gson = new Gson();
					APResource ap = gson.fromJson(js.toString(), APResource.class);
					if (virgula){
						query += ",";
					}
					query += " ('" + this.matricula + "', "
							+ "'" + ap.getBSSID() + "', "
							+ "'" + ap.getSSID() + "', "
							      + ap.getRSSI() + ", "
							      + sequencia + ", " 
							+ "'" + usuarioOrigem + "' ) ";
					virgula = true;
					sequencia++;
				}
				result = ConexaoBD.execute(con,query);
				if (result < 0){
					status = false;
				}
			}
		}
		
		return status;
	}
	
	public boolean updateToken(String token){
		boolean retorno = true;
		String query = "";
		if (this.matricula.isEmpty() || token.isEmpty()){
			retorno = false;
		}else{
			Connection con = ConexaoBD.getConexaoSQL();
			ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario_token WHERE matricula = '"+this.matricula+"'; ");
			try{
				if (rs.first()){
					query = "UPDATE usuario_token SET token = '" + token + "' WHERE matricula = '" + this.matricula + "'; ";
				}else{
					query = "INSERT INTO usuario_token (matricula, token) VALUES ('" + this.matricula + "', '" + token + "'); ";
				}
				int result = ConexaoBD.execute(con,query);
				if (result < 0){
					retorno = false;
				}else{
					retorno = true;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return retorno;
	}
	
	public boolean adicionar(String matriculaAmigo){
		boolean retorno = true;
		String query = "";
		if (this.matricula.isEmpty() || matriculaAmigo.isEmpty()){
			retorno = false;
			this.msg_erro = "Matricula do usuário/amigo em branco.";
		}else{
			Connection con = ConexaoBD.getConexaoSQL();
			ResultSet rsAmigo = ConexaoBD.consultar(con, "SELECT * FROM amigos WHERE matricula = '"+this.matricula+"' AND matricula_amigo = '" + matriculaAmigo + "' ; ");
			try{
				if (rsAmigo.first()){
					this.msg_erro = "Você já é amigo desse usuário.";
					return false;
				}else{
					ResultSet rsSolicitacao = ConexaoBD.consultar(con, "SELECT * FROM solicitacao_amigo WHERE matricula = '"+this.matricula+"' AND matricula_amigo = '" + matriculaAmigo + "' ; ");
					if (rsSolicitacao.first()){
						this.msg_erro = "Você já enviou uma solicitação de amizade para esse usuário.";
						return false;
					}else{
						rsSolicitacao = ConexaoBD.consultar(con, "SELECT * FROM solicitacao_amigo WHERE matricula = '"+ matriculaAmigo +"' AND matricula_amigo = '" + this.matricula + "' ; ");
						if (rsSolicitacao.first()){
							this.msg_erro = "Esse usuário já enviou uma solicitação de amizade para você.";
							return false;
						}else{
							query = " INSERT INTO solicitacao_amigo (matricula, matricula_amigo)"
									+ " VALUES ( '" + this.matricula + "', "
									         + " '" + matriculaAmigo + "' )";
							
							if (ConexaoBD.execute(con,query) < 0){
								this.msg_erro = "Ocorreram erros na inclusão da solicitação. Tente novamente.";
								return false;
							}else{
								retorno = true;
							}
						}
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return retorno;
	}
	
	public boolean excluir(String matriculaAmigo){
		boolean retorno = true;
		String query = "";
		if (this.matricula.isEmpty() || matriculaAmigo.isEmpty()){
			retorno = false;
			this.msg_erro = "Matricula do usuário/amigo em branco.";
		}else{
			Connection con = ConexaoBD.getConexaoSQL();
			ResultSet rsAmigo = ConexaoBD.consultar(con, "SELECT * FROM amigos WHERE matricula = '"+this.matricula+"' AND matricula_amigo = '" + matriculaAmigo + "' ; ");
			try{
				if (rsAmigo.first()){
					
					query = " DELETE FROM amigos WHERE (matricula = '"+this.matricula+"' AND matricula_amigo = '" + matriculaAmigo + "')"
							+ " OR (matricula = '"+matriculaAmigo+"' AND matricula_amigo = '" + this.matricula + "');  ";
					
					if (ConexaoBD.execute(con,query) < 0){
						this.msg_erro = "Ocorreram erros na exclusão do amigo. Tente novamente.";
						return false;
					}else{
						retorno = true;
					}
				}else{
					this.msg_erro = "Você não é amigo desse usuário.";
					return false;
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return retorno;
	}
	
	public boolean locate(String usuarioOrigem){
		boolean status = false;
		
		//primeiro verifica se o usuário tem o token do dispositivo cadastrado.
		Connection con = ConexaoBD.getConexaoSQL();
		ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario_token WHERE matricula = '"+this.matricula+"' ; ");
		String token = "";
		String query = "";
		int result = 0;
		try{
			if (rs.first()){
				token = rs.getString("token").trim();
				if(token.isEmpty()){
					this.msg_erro = "Usuário ainda não está disponível para localizar.";
					return false;
				}else{
					
					//Verifica as permissões de localização do usuário.
					if (! this.checkPermission(usuarioOrigem,this.matricula)){
						return false;
					}
					
					//Exclui as informações dos APS disponíveis desse usuário, e tenta recuperar as informações atualizadas.
					query = "DELETE FROM usuario_aps WHERE matricula = '" + this.matricula + "' AND usuario_origem = '"+usuarioOrigem+"' ; ";
					result = ConexaoBD.execute(con,query);
					if (result < 0){
						this.msg_erro = "Ocorreram erros ao atualizar as informações do usuário.";
						return false;
					}
					HttpClient client = HttpClientBuilder.create().build();
					HttpPost post = new HttpPost("https://fcm.googleapis.com/fcm/send");
					post.setHeader("Content-type", "application/json");
					post.setHeader("Authorization", "key=AIzaSyAet7Tpz_VnaLm5OLM9CKlGe-X8GDiwMCE");
					
					JSONObject message = new JSONObject();
					message.put("to", token);
					message.put("priority", "high");
					
					JSONObject data = new JSONObject();
					data.put("request", usuarioOrigem);
					
					message.put("data", data);
					
					post.setEntity(new StringEntity(message.toString(), "UTF-8"));
					
					HttpResponse response = client.execute(post);
					
					if (response.getStatusLine().getStatusCode() != 200){
						this.msg_erro = "Não foi possível encontrar o usuário. (Falha de conexão com o aparelho)";
						return false;
					}
					boolean continua = true;
					ResultSet rsAps = null;
					int tentativa = 0;
					while(continua){
						if (tentativa >= 5){
							continua = false;
						}
						rsAps = ConexaoBD.consultar(con, "SELECT * FROM usuario_aps WHERE matricula = '"+this.matricula+"' AND usuario_origem = '"+usuarioOrigem+"' ORDER BY sequencia; ");
						 if (rsAps.first()){
							 continua = false;
						 }else{
							 Thread.sleep(500);
							 tentativa++;
						 }
					}
					
					if (rsAps.first()){
						PredictionRequest request = new PredictionRequest();
						request.addAccessPoint(rsAps.getString("BSSID").trim(), rsAps.getFloat("RSSI"));
						while(rsAps.next()){
							request.addAccessPoint(rsAps.getString("BSSID").trim(), rsAps.getFloat("RSSI"));
						}
						
						PredictionService predServ = PredictionService.getInstance();
						Room room = predServ.predict(request);
						this.msg_erro = room.getName().trim();
						status = true;
					}else{
						this.msg_erro = "Não foi possível encontrar o usuário. (Falha na obtenção de suas informações)";
						return false;
					}					
				}
			}else{
				this.msg_erro = "Usuário ainda não está disponível para localizar.";
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			this.msg_erro = "Ocorreram erros no programa. " + e.getMessage();
		}
		return status;
	}
	
	public List<Usuario> getSolicitacoesAmizade(String matricula){
		List<Usuario> usuarios = new ArrayList<Usuario>();
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
			usuarios = null;
		}else{
			ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario "
					+ " WHERE usuario.matricula IN ( SELECT solicitacao_amigo.matricula FROM solicitacao_amigo"
											 	 + "  WHERE matricula_amigo = '" + matricula + "' ) ORDER BY nome; ");
			try{
				boolean achou = false;
				while (rs.next()){
					achou = true;
					usuarios.add(new Usuario(rs.getString("matricula"),
							rs.getString("nome"),
							rs.getString("email"),
							"senha",
							rs.getString("data_nascimento"),
							rs.getString("first_login"),
							rs.getInt("nivel_privacidade")));
				}
				if (!achou){
					setMsg_erro("Nenhum usuário encontrado.");
				}
			}catch (SQLException e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
				usuarios = null;
			}catch (Exception e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
				usuarios = null;
			}
		}
		return usuarios;
	}
	
	public boolean atualizaSolicitacaoAmizade(String matriculaAmigo, String aceito){
		boolean status = false;
		
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
		}else{
			ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM solicitacao_amigo "
											 	 + "  WHERE matricula_amigo = '" + this.matricula + "' "
											 	 + "    AND matricula       = '" + matriculaAmigo + "' ; ");
			try{
				if (rs.first()){
					if (aceito.equals("S")){
						String query = " INSERT INTO amigos (matricula, matricula_amigo) ";
						query += " VALUES ('"+this.matricula+"', '"+matriculaAmigo+"'), ";
						query +=         "('"+matriculaAmigo+"', '"+this.matricula+"'); ";
						query += " DELETE FROM solicitacao_amigo "
								+ " WHERE matricula_amigo = '" + this.matricula + "' "
								+ "   AND matricula       = '" + matriculaAmigo + "' ; ";
						int result = ConexaoBD.execute(con,query);
						if (result < 0){
							setMsg_erro("Ocorreram erros ao aceitar a solicitação. Tente novamente.");
							return false;
						}else{
							status = true;
						}
					}else{
						int result = ConexaoBD.execute(con," DELETE FROM solicitacao_amigo "
								+ " WHERE matricula_amigo = '" + this.matricula + "' "
								+ "   AND matricula       = '" + matriculaAmigo + "' ; ");
						if (result < 0){
							setMsg_erro("Ocorreram erros ao recusar a solicitação. Tente novamente.");
							return false;
						}else{
							status = true;
						}
					}
				}else{
					setMsg_erro("Não existem solicitações de amizade para esse usuário.");
					return false;
				}
				
			}catch (SQLException e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
			}catch (Exception e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
			}
		}
		
		return status;
	}
	
	public boolean bloquear(String matriculaAmigo, String block){
		boolean status = false;
		
		Connection con = ConexaoBD.getConexaoSQL();
		
		if (con == null){
			setMsg_erro(ConexaoBD.msgErro);
		}else{
			String query = " SELECT * FROM amigos_bloqueados "
					+ " WHERE matricula = '" + this.matricula + "'"
					+ " AND   matricula_amigo = '" + matriculaAmigo + "'; ";
			ResultSet rs = ConexaoBD.consultar(con, query);
			try{
				if (rs.first()){
					if (block.equals("S")){
						this.msg_erro = "Usuário já está bloqueado.";
						return false;
					}else{
						int result = ConexaoBD.execute(con," DELETE FROM amigos_bloqueados "
								+ " WHERE matricula = '" + this.matricula + "'"
								+ " AND   matricula_amigo = '" + matriculaAmigo + "'; ");
						if (result < 0){
							setMsg_erro("Ocorreram erros ao desbloquear o amigo. Tente novamente.");
							return false;
						}else{
							status = true;
						}
					}
				}else{
					if(block.equals("S")){
						int result = ConexaoBD.execute(con, "INSERT INTO amigos_bloqueados (matricula, matricula_amigo)"
								+ " VALUES ('"+this.matricula+"', '"+matriculaAmigo+"');  ");
						if (result < 0){
							setMsg_erro("Ocorreram erros ao bloquear o amigo. Tente novamente.");
							return false;
						}else{
							status = true;
						}
					}else{
						this.msg_erro = "Amigo já está desbloqueado.";
						return false;
					}
				}
				
			}catch (SQLException e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
			}catch (Exception e){
				e.printStackTrace();
				setMsg_erro("Erro ao buscar usuarios." + e.getMessage());
			}
		}
		
		return status;
	}

	private boolean checkPermission(String userOrigem, String userDestino){
		
		String query = "";
		
		query = "SELECT * FROM amigos_bloqueados WHERE matricula_amigo = '" + userOrigem + "' AND matricula = '" + userDestino + "';";
		Connection con = ConexaoBD.getConexaoSQL();
		ResultSet rs = ConexaoBD.consultar(con, query);
		try{
			if (rs.first()){
				this.setMsg_erro("Você está bloqueado por esse usuário.");
				return false;
			}else{
				query = "SELECT * FROM amigos_bloqueados WHERE matricula_amigo = '" + userDestino + "' AND matricula = '" + userOrigem + "';";
				rs = ConexaoBD.consultar(con, query);
				if (rs.first()){
					this.setMsg_erro("Você bloqueou esse usuário. Para localizá-lo, é necessário desbloqueá-lo.");
					return false;
				}else{
					query = " SELECT * FROM usuario WHERE matricula = '" + userDestino + "'; ";
					rs = ConexaoBD.consultar(con, query);
					if (rs.first()){
						int nivelPrivacidade = rs.getInt("nivel_privacidade");
						System.out.println(nivelPrivacidade);
						System.out.println(userDestino);
						if (nivelPrivacidade == 0){ //todos
							return true;
						}else{
							if (nivelPrivacidade == 3){ // ninguém
								this.setMsg_erro("Não é possível localizar esse usuário no momento. Usuário não está compartilhando a sua localização.");
								return false;
							}else{
								if (nivelPrivacidade == 1){ //somente amigos
									query = "SELECT * FROM amigos WHERE matricula = '" + userDestino + "' AND matricula_amigo = '" + userOrigem + "';";
									rs = ConexaoBD.consultar(con, query);
									if (rs.first()){
										return true;
									}else{
										this.setMsg_erro("Você não é amigo desse usuário. Compartilhamento de localização desse usuário está habilitado apenas para seus amigos.");
										return false;
									}
								}
							}
						}
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	//-----------------------------------
	//Getters/Setters
	//-----------------------------------
	public String getMsg_erro() {
		return msg_erro;
	}
	public void setMsg_erro(String msg_erro) {
		this.msg_erro = msg_erro;
	}
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public String getData_nascimento() {
		return data_nascimento;
	}
	public void setData_nascimento(String data_nascimento) {
		if ( data_nascimento != null){
			this.data_nascimento = data_nascimento.replace("/","");
		}else{
			this.data_nascimento = data_nascimento;
		}
		
	}
	public String getFirst_login() {
		return first_login;
	}
	public void setFirst_login(String first_login) {
		this.first_login = first_login;
	}
	public int getNivel_privacidade() {
		return nivel_privacidade;
	}
	public void setNivel_privacidade(int nivel_privacidade) {
		this.nivel_privacidade = nivel_privacidade;
	}
}
