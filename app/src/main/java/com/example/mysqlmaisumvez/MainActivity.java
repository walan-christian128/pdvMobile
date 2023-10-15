package com.example.mysqlmaisumvez;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText etddescricao, etdpreceoCompra, etdPrecoVenda, etdqtdEstoque, etdcodigo;
    Button btnbusca, btninserir, btneditar, btnapagar;
    Spinner cboFornecedor;
    RequestQueue requestQueue;
    private static final String URL1 = "http://192.168.1.9/distribuidora/InserirProduto.php";
    List<String> nomesFornecedoresList;
    Fornecedores fornecedores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);
        cboFornecedor = findViewById(R.id.cboFornecedor);

        initUI();
        obterFornecedoresDoWebService();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nomesFornecedoresList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboFornecedor.setAdapter(adapter);

        btninserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarProduto();
            }
        });
        btnbusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarProduto("http://192.168.1.9/distribuidora/buscarProdutos.php?id="+etdcodigo.getText()+"");
            }
        });
        cboFornecedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               fornecedores =(Fornecedores) parent.getItemAtPosition(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
               fornecedores= null;
            }
        });
    }

    private void obterFornecedoresDoWebService() {
        String URL = "http://192.168.1.9/distribuidora/listarFornecedores.php";


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    nomesFornecedoresList = new ArrayList<String>();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject fornecedor = response.getJSONObject(i);
                        String nomeFornecedor = fornecedor.getString("nome");
                        nomesFornecedoresList.add(nomeFornecedor);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Lidar com erros na solicitação
                Toast.makeText(MainActivity.this, "Erro na solicitação: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        requestQueue.add(jsonArrayRequest);
    }

    private void buscarProduto(String URL2) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    etddescricao.setText(jsonObject.getString("descricao"));
                    etdpreceoCompra.setText(jsonObject.getString("preco_de_compra"));
                    etdPrecoVenda.setText(jsonObject.getString("preco_de_venda"));
                    etdqtdEstoque.setText(jsonObject.getString("qtd_estoque"));
                    String valorFornecedor = jsonObject.getString("for_id");
                    int posicao = nomesFornecedoresList.indexOf(valorFornecedor);
                    if (posicao >= 0) {
                        cboFornecedor.setSelection(posicao);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Erro na resposta JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERRO NA CONEXÃO: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(stringRequest);
    }

    private void initUI() {
        etddescricao = findViewById(R.id.descricao);
        etdpreceoCompra = findViewById(R.id.precoCompra);
        etdPrecoVenda = findViewById(R.id.precoVenda);
        etdqtdEstoque = findViewById(R.id.qtdEstoque);
        btnbusca = findViewById(R.id.buscar);
        btnapagar = findViewById(R.id.apagar);
        btneditar = findViewById(R.id.editar);
        btninserir = findViewById(R.id.inserir);
        cboFornecedor = findViewById(R.id.cboFornecedor);
    }

    private void cadastrarProduto() {
        String descricao = etddescricao.getText().toString().trim();
        String precoCompraStr = etdpreceoCompra.getText().toString().trim();
        String precoVendaStr = etdPrecoVenda.getText().toString().trim();
        String qtdEstoqueStr = etdqtdEstoque.getText().toString().trim();
        String codigoFornecStr = String.valueOf(cboFornecedor.getSelectedItem());


        try {
            float precoCompra = Float.parseFloat(precoCompraStr);
            float precoVenda = Float.parseFloat(precoVendaStr);
            int qtdEstoque = Integer.parseInt(qtdEstoqueStr);
            int codigoFornec = Integer.parseInt(codigoFornecStr);


            enviarCadastroParaServidor(descricao, precoCompra, precoVenda, qtdEstoque, codigoFornec);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Valores inválidos", Toast.LENGTH_SHORT).show();
            Log.e("NumberFormatException", "Erro de conversão de tipo: " + e.getMessage());
        }
    }

    private void enviarCadastroParaServidor(String descricao, float precoCompra, float precoVenda, int qtdEstoque, int cboFornecedor) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Aqui você lida com a resposta do servidor, por exemplo, exibir uma mensagem de sucesso
                        Toast.makeText(MainActivity.this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.e("Volley Error", "Erro ao enviar a solicitação: " + error.toString());
                        Toast.makeText(MainActivity.this, "Erro ao cadastrar: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("descricao", descricao);
                params.put("preco_de_compra", String.valueOf(precoCompra));
                params.put("preco_de_venda", String.valueOf(precoVenda));
                params.put("qtd_estoque", String.valueOf(qtdEstoque));
                params.put("for_id", String.valueOf(cboFornecedor));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
