package com.example.consultacep.api

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.consultacep.R
import com.example.consultacep.model.EnderecoCep
import org.json.JSONObject

class CepRepository(private val context: Context) {

    fun consultarCep(
        cep: String,
        onSuccess: (EnderecoCep) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "https://viacep.com.br/ws/$cep/json/"

        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response -> processarResposta(response, onSuccess, onError) },
            { onError("erro_rede") }
        )

        Volley.newRequestQueue(context.applicationContext).add(request)
    }

    private fun processarResposta(
        response: JSONObject,
        onSuccess: (EnderecoCep) -> Unit,
        onError: (String) -> Unit
    ) {
        if (response.optBoolean("erro", false)) {
            onError("cep_nao_encontrado")
            return
        }

        val cep = response.optString("cep", "")
        val logradouro = response.optString("logradouro", "")
        val bairro = response.optString("bairro", "")
        val localidade = response.optString("localidade", "")
        val uf = response.optString("uf", "")
        val ddd = response.optString("ddd", "")

        if (cep.isBlank() || localidade.isBlank()) {
            onError("resposta_invalida")
            return
        }

        onSuccess(
            EnderecoCep(
                cep = cep,
                logradouro = logradouro.ifBlank { context.getString(R.string.valor_vazio) },
                bairro = bairro.ifBlank { context.getString(R.string.valor_vazio) },
                localidade = localidade,
                uf = uf,
                ddd = ddd.ifBlank { context.getString(R.string.valor_vazio) }
            )
        )
    }
}
