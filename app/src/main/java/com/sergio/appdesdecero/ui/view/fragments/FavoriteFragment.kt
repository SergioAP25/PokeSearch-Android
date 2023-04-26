package com.sergio.appdesdecero.ui.view.fragments

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sergio.appdesdecero.R
import com.sergio.appdesdecero.databinding.FragmentFavoriteBinding
import com.sergio.appdesdecero.domain.model.FilteredPokemon
import com.sergio.appdesdecero.ui.view.FullScreenImageActivity
import com.sergio.appdesdecero.ui.viewmodel.PokemonDetailViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var pokemonDetailViewModel: PokemonDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        pokemonDetailViewModel = ViewModelProvider(this).get(PokemonDetailViewModel::class.java)
        configSwipe()
        if(pokemonDetailViewModel.scope!=null){
            pokemonDetailViewModel.scope!!.cancel()
        }
        pokemonDetailViewModel.randomPokemon()
        pokemonDetailViewModel.pokemonModel.observe(viewLifecycleOwner, Observer {pokemon ->
            Picasso.get().load(pokemon?.sprites?.front_default).into(binding.pokemonImage)
            binding.pokemonName.text = pokemon?.name

            updateStatBar(binding.hp, pokemon?.stats?.get(0)?.base_stat)
            updateStatBar(binding.attack, pokemon?.stats?.get(1)?.base_stat)
            updateStatBar(binding.defense, pokemon?.stats?.get(2)?.base_stat)
            updateStatBar(binding.specialAttack, pokemon?.stats?.get(3)?.base_stat)
            updateStatBar(binding.specialDefense, pokemon?.stats?.get(4)?.base_stat)
            updateStatBar(binding.speed, pokemon?.stats?.get(5)?.base_stat)

            bindTypes(pokemon)

            binding.height1.text = (pokemon?.height?.toFloat()?.div(10)).toString()+" m"
            binding.weight1.text = (pokemon?.weight?.toFloat()?.div(10)).toString()+" kg"

            binding.pokemonImage.setOnClickListener {
                navigateToFullImage(pokemon?.sprites?.front_default!!)
            }

            CoroutineScope(Dispatchers.IO).launch {
                if(!pokemonDetailViewModel.isFavoritePokemon(pokemon!!.name)){
                    binding.boton.setBackgroundResource(R.drawable.baseline_star_border_24)
                }
                else{
                    binding.boton.setBackgroundResource(R.drawable.baseline_star_24)
                }
            }
            binding.boton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    if(!pokemonDetailViewModel.isFavoritePokemon(pokemon!!.name)){
                        pokemonDetailViewModel.addFavoritePokemon(pokemon.name)
                        binding.boton.setBackgroundResource(R.drawable.baseline_star_24)
                    }
                    else{
                        pokemonDetailViewModel.removeFavoritePokemon(pokemon.name)
                        binding.boton.setBackgroundResource(R.drawable.baseline_star_border_24)
                    }
                }
            }

        })
        return binding.root
    }

    private fun configSwipe(){
        binding.homeSwipe.setOnRefreshListener {
            parentFragmentManager.beginTransaction()
                .detach(this).commit()
            parentFragmentManager.beginTransaction()
                .attach(this).commit()
            binding.homeSwipe.isRefreshing = false
        }
    }

    private fun bindTypes(pokemon: FilteredPokemon?){
        binding.type1.setImageResource(0)
        binding.type2.setImageResource(0)
        if (pokemon?.types?.size==1){
            binding.type1.setImageResource(getTypeImage(pokemon.types.get(0).type.name))
        }
        else{
            binding.type1.setImageResource(getTypeImage(pokemon?.types?.get(0)?.type?.name))
            binding.type2.setImageResource(getTypeImage(pokemon?.types?.get(1)?.type?.name))
        }
    }

    private fun getTypeImage(type: String?): Int {
        var result: Int = 0
        when(type) {
            "bug" -> result = R.drawable.bug
            "dark" -> result = R.drawable.dark
            "dragon" -> result = R.drawable.dragon
            "electric" -> result = R.drawable.electric
            "fairy" -> result = R.drawable.fairy
            "fighting" -> result = R.drawable.fighting
            "fire" -> result = R.drawable.fire
            "flying" -> result = R.drawable.flying
            "ghost" -> result = R.drawable.ghost
            "grass" -> result = R.drawable.grass
            "ground" -> result = R.drawable.ground
            "ice" -> result = R.drawable.ice
            "normal" -> result = R.drawable.normal
            "poison" -> result = R.drawable.poison
            "psychic" -> result = R.drawable.psychic
            "rock" -> result = R.drawable.rock
            "steel" -> result = R.drawable.steel
            "water" -> result = R.drawable.water
        }
        return result
    }

    private fun updateStatBar(view: View, stat: Int?){
        val params = view.layoutParams
        params.height = pxToDp(stat?.toFloat())
        view.layoutParams = params
    }

    private fun pxToDp(px :Float?): Int{
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px!!, resources.displayMetrics)
            .roundToInt()
    }

    private fun navigateToFullImage(image: String){
        val intent = Intent(context, FullScreenImageActivity::class.java)
        intent.putExtra(FullScreenImageActivity.EXTRA_IMAGE, image)
        startActivity(intent)
    }

}