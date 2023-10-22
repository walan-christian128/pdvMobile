package com.example.mysqlmaisumvez;

public class Fornecedores extends Clientes  {
    private int id;

    public Fornecedores(int id, String nome) {
        this.id = id ;
        this.nome = nome;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    private String nome;
    private String cnpj;

    public String toString(){
        return nome;

    }



    }
