package com.example.consultacep

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.example.consultacep.api.CepRepository
import com.example.consultacep.databinding.ActivityMainBinding
import com.example.consultacep.model.EnderecoCep

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cepRepository: CepRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cepRepository = CepRepository(this)
        configurarEventos()
    }

    private fun configurarEventos() {
        binding.btnConsultar.setOnClickListener { realizarConsulta() }

        binding.etCep.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                realizarConsulta()
                true
            } else {
                false
            }
        }
    }

    private fun realizarConsulta() {
        ocultarErro()
        ocultarResultado()

        val cepDigitado = binding.etCep.text?.toString()?.trim().orEmpty()

        if (cepDigitado.isEmpty()) {
            exibirErro(getString(R.string.erro_campo_vazio))
            binding.tilCep.error = getString(R.string.erro_campo_vazio)
            return
        }

        binding.tilCep.error = null

        if (!cepValido(cepDigitado)) {
            exibirErro(getString(R.string.erro_cep_invalido))
            return
        }

        binding.btnConsultar.isEnabled = false
        binding.btnConsultar.text = getString(R.string.consultando)

        cepRepository.consultarCep(
            cep = cepDigitado,
            onSuccess = { endereco ->
                runOnUiThread {
                    restaurarBotao()
                    exibirResultado(endereco)
                }
            },
            onError = { tipoErro ->
                runOnUiThread {
                    restaurarBotao()
                    val mensagem = when (tipoErro) {
                        "cep_nao_encontrado" -> getString(R.string.erro_cep_nao_encontrado)
                        "resposta_invalida" -> getString(R.string.erro_resposta_invalida)
                        else -> getString(R.string.erro_requisicao)
                    }
                    exibirErro(mensagem)
                }
            }
        )
    }

    private fun cepValido(cep: String): Boolean {
        return cep.length == 8 && cep.all { it.isDigit() }
    }

    private fun exibirResultado(endereco: EnderecoCep) {
        binding.tvCep.text = endereco.cep
        binding.tvLogradouro.text = endereco.logradouro
        binding.tvBairro.text = endereco.bairro
        binding.tvCidade.text = endereco.localidade
        binding.tvUf.text = endereco.uf
        binding.tvDdd.text = endereco.ddd
        binding.cardResultado.visibility = View.VISIBLE
    }

    private fun exibirErro(mensagem: String) {
        binding.tvErro.text = mensagem
        binding.tvErro.visibility = View.VISIBLE
    }

    private fun ocultarErro() {
        binding.tvErro.visibility = View.GONE
    }

    private fun ocultarResultado() {
        binding.cardResultado.visibility = View.GONE
    }

    private fun restaurarBotao() {
        binding.btnConsultar.isEnabled = true
        binding.btnConsultar.text = getString(R.string.btn_consultar)
    }
}
