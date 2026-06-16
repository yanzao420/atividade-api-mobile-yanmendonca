package com.example.consultacep.model

data class EnderecoCep(
    val cep: String,
    val logradouro: String,
    val bairro: String,
    val localidade: String,
    val uf: String,
    val ddd: String
)
