package edu.kokhan.auzoo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import edu.kokhan.auzoo.R
import kotlinx.android.synthetic.main.gallery_recycler_view_item.view.*
import kotlinx.android.synthetic.main.row_animal.view.*
import java.io.File

class GalleryListAdapter(
    private val photoFiles: List<File>,
    private val clickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<GalleryListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.gallery_recycler_view_item,
                parent,
                false
            ),
            clickListener
        )
    }

    override fun getItemCount(): Int = photoFiles.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(
        view: View, listener: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val photo: ImageView = view.iv_galleryImage

        init {
            view.imageCardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener(position)
                }
            }
        }

        fun bind(position: Int) {
            Glide.with(photo.context)
                .load(photoFiles[position].path)
                .centerInside()
                .transform(RoundedCorners(58))
                .into(photo)
        }
    }
}