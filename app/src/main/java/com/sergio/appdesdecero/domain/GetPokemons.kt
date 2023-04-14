package com.sergio.appdesdecero.domain

import android.util.Log
import com.sergio.appdesdecero.data.PokemonRepository
import com.sergio.appdesdecero.domain.model.Pokemon
import javax.inject.Inject

class GetPokemons @Inject constructor(
    private val repository: PokemonRepository
){
    suspend operator fun invoke(): Pokemon?{
        val pokemons = repository.getAllPokemonsFromApi()
        return pokemons
    }
}