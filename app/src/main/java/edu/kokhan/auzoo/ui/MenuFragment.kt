package edu.kokhan.auzoo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.kokhan.auzoo.R
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        scanBtn.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToScanFragment())
        }
        bookBtn.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToBookFragment())
        }
        gameBtn.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToGameFragment())
        }
        galleryBtn.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToGalleryFragment())
        }
        optionsBtn.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToOptionsFragment())
        }
        aboutBtn.setOnClickListener {
            findNavController().navigate(MenuFragmentDirections.actionMenuFragmentToAboutFragment())
        }
    }

}
