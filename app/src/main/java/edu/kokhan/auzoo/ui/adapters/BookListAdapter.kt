package edu.kokhan.auzoo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.model.Animal
import kotlinx.android.synthetic.main.row_animal.view.*

class BookListAdapter(
    private val clickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<BookListAdapter.ViewHolder>() {

    var data: List<Animal> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_animal,
                parent,
                false
            ),
            clickListener
        )
    }

    override fun getItemCount(): Int = data.size

    fun updateData(it: List<Animal>) {
        data = it
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(
        view: View, listener: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.tv_animalName
        private val description: TextView = view.tv_animalShortDescription
        private val photo: ImageView = view.iv_animalPhoto

        init {
            view.rootCardView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener(position)
                }
            }
        }

        fun bind(position: Int) {
            name.text = data[position].name.capitalize()
            description.text = data[position].shortDescription
            Glide.with(photo.context)
                .load(data[position].photoUrl)
                .centerInside()
                .transform(RoundedCorners(58))
                .into(photo)
        }
    }
}