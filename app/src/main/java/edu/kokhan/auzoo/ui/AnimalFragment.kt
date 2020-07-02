package edu.kokhan.auzoo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.database.FirestoreDatabase
import edu.kokhan.auzoo.model.Animal
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_animal.*
import org.koin.android.ext.android.inject

class AnimalFragment : Fragment() {

    private val firestore: FirestoreDatabase by inject()
    private val args: AnimalFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animal, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val disposable: Disposable = firestore.getAnimalByName(args.animalName)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {

            }
            .subscribe({ animal ->
                bindData(animal)
                Log.d("RxLog", animal.description)
            }, {
                //Snackbar with action lambda
                // Cant load data
                Log.e("AnimalFragment", "onError $it")
            })
    }

    private fun bindData(animal: Animal) {
        tv_animalName.text = animal.name.capitalize()
        tv_lengthData.text = animal.length
        tv_heightData.text = animal.height
        tv_massData.text = animal.mass
        tv_animalDescriptionText.text = animal.description
        Glide.with(iv_mainPhoto.context)
            .load(animal.photoUrl)
            //TODO create viewable image
            .centerCrop()
            .centerInside()
            .transform(RoundedCorners(58))
            .into(iv_mainPhoto)
        btn_arViewButton.setOnClickListener {
            findNavController().navigate(
                AnimalFragmentDirections.actionAnimalFragmentToArViewerFragment(
                    animal.name
                )
            )
        }
    }
}
