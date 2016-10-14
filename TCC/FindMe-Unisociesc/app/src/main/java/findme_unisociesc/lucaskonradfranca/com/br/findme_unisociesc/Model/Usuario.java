package findme_unisociesc.lucaskonradfranca.com.br.findme_unisociesc.Model;

import java.io.Serializable;

public class Usuario implements Serializable{
    public Usuario() { }
    public Usuario(String matricula, String nome, String email, String senha, String data_nascimento, String first_login) {
        this.matricula = matricula;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.data_nascimento = data_nascimento;
        this.first_login = first_login;
    }

    //Dados do usuário
    private String matricula = "";
    private String nome = "";
    private String email = "";
    private String senha = "";
    private String data_nascimento = "";
    private String first_login = "";

    private String msg_erro = "";

    //-----------------------------------
    //Getters/Setters
    //-----------------------------------
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
        this.data_nascimento = data_nascimento;
    }
    public String getFirst_login() {
        return first_login;
    }
    public void setFirst_login(String first_login) {
        this.first_login = first_login;
    }
    public String getMsg_erro() {
        return msg_erro;
    }
    public void setMsg_erro(String msg_erro) {
        this.msg_erro = msg_erro;
    }
}
