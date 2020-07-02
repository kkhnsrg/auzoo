package edu.kokhan.auzoo.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.ui.adapters.BookListAdapter
import edu.kokhan.auzoo.database.FirestoreDatabase
import edu.kokhan.auzoo.model.Animal
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_book.*
import org.koin.android.ext.android.inject

class BookFragment : Fragment() {

    private val firestore: FirestoreDatabase by inject()

    private lateinit var adapter: BookListAdapter

    var animals = arrayListOf<Animal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = BookListAdapter { position ->
            findNavController().navigate(BookFragmentDirections.actionBookFragmentToAnimalFragment(animals[position].name))
        }
        rv_AnimalList.adapter = adapter

        firestore.getAllAnimals()
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                // show progress
            }

            .subscribe({
                animals.addAll(it)
                adapter.updateData(animals)
            }, {
                //Snackbar with action lambda
                Log.e("BookFragment", "onError $it")
            })
    }

}
