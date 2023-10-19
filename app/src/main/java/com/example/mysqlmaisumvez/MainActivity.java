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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText etddescricao, etdpreceoCompra, etdPrecoVenda, etdqtdEstoque, etdcodigo;
    Button btnbusca, btninserir, btneditar, btnapagar;



    RequestQueue requestQueue;
    private static final String URL1 = "http://192.168.1.9/distribuidora/InserirProduto.php";
    private static final String URL2 = "http://192.168.1.9/distribuidora/buscarProdutos.php?id=";
    private static final String URL3 = "http://192.168.1.9/distribuidora/listarFornecedores.php";
    Spinner cboFornecedor;
    ArrayList<String> fornecedoresList = new ArrayList<>();
    ArrayAdapter<String> forned;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        initUI();
        cboFornecedor = findViewById(R.id.spinnerforcedor);
        requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL3, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("fornecedores");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String fornecedorNome = jsonObject.optString("nome");
                        fornecedoresList.add(fornecedorNome);
                    }
                    forned = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, fornecedoresList);
                    forned.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cboFornecedor.setAdapter(forned);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        requestQueue.add(jsonObjectRequest);
        btninserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarProduto();
            }
        });
        btnbusca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarProduto(URL2 + etdcodigo.getText());
            }
        });



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

        requestQueue.add(stringRequest); // Adicione a solicitação à fila.
    }

    private void initUI() {
        etdcodigo = findViewById(R.id.codigo);
        etddescricao = findViewById(R.id.descricao);
        etdpreceoCompra = findViewById(R.id.precoCompra);
        etdPrecoVenda = findViewById(R.id.precoVenda);
        etdqtdEstoque = findViewById(R.id.qtdEstoque);
        cboFornecedor = findViewById(R.id.spinnerforcedor);

        btnbusca = findViewById(R.id.buscar);
        btnapagar = findViewById(R.id.apagar);
        btneditar = findViewById(R.id.editar);
        btninserir = findViewById(R.id.inserir);
    }

    private void cadastrarProduto() {
        String descricao = etddescricao.getText().toString().trim();
        String precoCompraStr = etdpreceoCompra.getText().toString().trim();
        String precoVendaStr = etdPrecoVenda.getText().toString().trim();
        String qtdEstoqueStr = etdqtdEstoque.getText().toString().trim();

        // Validação de entrada (adicione sua validação aqui)

        try {
            float precoCompra = Float.parseFloat(precoCompraStr);
            float precoVenda = Float.parseFloat(precoVendaStr);
            int qtdEstoque = Integer.parseInt(qtdEstoqueStr);

            // Agora você pode usar os valores
            Fornecedores fornecedorSelecionado = (Fornecedores) cboFornecedor.getSelectedItem();
            int codigoFornec = fornecedorSelecionado.getId();
            enviarCadastroParaServidor(descricao, precoCompra, precoVenda, qtdEstoque, codigoFornec);
        } catch (NumberFormatException e) {
            Toast.makeText(MainActivity.this, "Valores inválidos", Toast.LENGTH_SHORT).show();
            Log.e("NumberFormatException", "Erro de conversão de tipo: " + e.getMessage());
        }
    }

    private void enviarCadastroParaServidor(String descricao, float precoCompra, float precoVenda, int qtdEstoque, int codigoFornec) {
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
                        // Aqui você lida com os erros da solicitação, por exemplo, exibir uma mensagem de erro
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
                params.put("for_id", String.valueOf(codigoFornec));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
