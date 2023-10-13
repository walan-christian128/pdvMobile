package com.example.mysqlmaisumvez;

public class Fornecedores extends Clientes  {
    private String cnpj;

    //atributos//
    public String getCnpj() {
        return cnpj;
    }

    //gets e stter/
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    @Override
    public String toString(){
        return this.getNome();

    }
    }
