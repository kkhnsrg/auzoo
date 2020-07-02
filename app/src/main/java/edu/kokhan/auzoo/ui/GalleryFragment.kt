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
import edu.kokhan.auzoo.ui.adapters.GalleryListAdapter
import edu.kokhan.auzoo.utils.createDirIfNotExist
import edu.kokhan.auzoo.utils.getPhotoGalleryPath
import edu.kokhan.auzoo.utils.isImage
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_book.*
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

private const val EMPTY_DIR_MSG = "Empty directory"

/**
 * A simple [Fragment] subclass.
 */
class GalleryFragment : Fragment(), CoroutineScope by MainScope() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        launch(Dispatchers.Main) {
            val createLists = prepareData()
            launch(Dispatchers.Main) {
                val galleryAdapter = GalleryListAdapter(createLists) { position ->
                    findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToImageFragment(createLists[position].path))
                }

                if (createLists.isNotEmpty())
                    tv_gallery_empty.visibility = View.INVISIBLE

                galleryRecyclerView.adapter = galleryAdapter
            }
        }

        super.onActivityCreated(savedInstanceState)
    }

    private fun prepareData(): List<File> {
        val path = getPhotoGalleryPath()

        Log.d("Files", "Path: $path")
        val directory = File(path)
//        directory.createDirIfNotExist()

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val files = directory.listFiles()

        Log.d(
            "Files",
            files?.toMutableList()?.filter {
                it.isFile && it.isImage()
            }?.toString()
                ?: EMPTY_DIR_MSG
        )

        return files?.toMutableList()?.filter { it.isFile && it.isImage() }
            ?: emptyList()
    }
}

