package edu.kokhan.auzoo.ui


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.utils.FILE_PROVIDER_AUTHORITY
import edu.kokhan.auzoo.utils.IMG_FORMAT
import kotlinx.android.synthetic.main.fragment_image.*
import java.io.File

private const val DELETE_ERROR_MSG = "Delete error"
private const val SHARE_INTENT_MSG = "Share image"

/**
 * A simple [Fragment] subclass.
 */
class ImageFragment : Fragment() {

    private val args: ImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        context?.let {
            Glide.with(it)
                .load(args.photoPath)
                .centerInside()
                .into(iv_photoPreview)
        }
        btn_deletePhoto.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure?")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    if (File(args.photoPath).delete()) {
                        activity?.onBackPressed()
                    } else {
                        Toast.makeText(context, DELETE_ERROR_MSG, Toast.LENGTH_SHORT).show()
                    }
                }.show()
        }
        btn_sharePhoto.setOnClickListener {
            shareImage(File(args.photoPath))
        }
        super.onActivityCreated(savedInstanceState)
    }

    private fun shareImage(file: File) {
        val fileUri = FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, file)

        val sharingIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/$IMG_FORMAT"
            putExtra(Intent.EXTRA_STREAM, fileUri)
        }
        startActivity(Intent.createChooser(sharingIntent, SHARE_INTENT_MSG))
    }

}
