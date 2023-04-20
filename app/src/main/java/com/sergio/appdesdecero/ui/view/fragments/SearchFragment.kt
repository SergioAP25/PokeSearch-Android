package com.sergio.appdesdecero.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sergio.appdesdecero.databinding.FragmentSearchBinding
import com.sergio.appdesdecero.ui.view.DetailActivity
import com.sergio.appdesdecero.ui.view.recyclerview.PokemonAdapter
import com.sergio.appdesdecero.ui.viewmodel.PokemonViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var pokemonViewModel: PokemonViewModel
    private lateinit var adapter: PokemonAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        pokemonViewModel = ViewModelProvider(this).get(PokemonViewModel::class.java)
        initUI()
        return binding.root

    }


    private fun initUI(){
        adapter = PokemonAdapter(emptyList(), this::navigatetoDetail, this::addFavorite,
            this::removeFavorite, this::isFavorite)
        binding.rvPokemon.setHasFixedSize(true)
        binding.rvPokemon.layoutManager = LinearLayoutManager(context)
        binding.rvPokemon.adapter = adapter
        binding.searchbar.setOnQueryTextListener(object:
            SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                pokemonViewModel.onCreate(query.orEmpty())
                pokemonViewModel.pokemonModel.observe(viewLifecycleOwner, Observer {pokemon ->
                    adapter.setData(pokemon)
                })
                return false
            }
        })
    }

    private fun navigatetoDetail(name:String){
        val intent = Intent(context, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_NAME, name)
        startActivity(intent)
    }

    private suspend fun addFavorite(name: String){
        pokemonViewModel.addFavoritePokemon(name)
    }

    private suspend fun removeFavorite(name: String){
        pokemonViewModel.removeFavoritePokemon(name)
    }

    private suspend fun isFavorite(name: String): Boolean{
        return pokemonViewModel.isFavoritePokemon(name)
    }
}