package models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			ResultSet rs = ConexaoBD.consultar(con, "SELECT * FROM usuario WHERE matricula <> '"+matricula+"' ORDER BY nome; ");
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
