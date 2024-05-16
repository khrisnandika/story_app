package com.example.storyapps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.storyapps.data.model.ListStoryItem
import com.example.storyapps.databinding.ListStoryBinding

class AdapterStory (private val dataInformation : MutableList<ListStoryItem> = mutableListOf(),
                    private val clickListener: (ListStoryItem) -> Unit) : RecyclerView.Adapter<AdapterStory.StoryViewHolder>() {

    fun setUsersData(data: MutableList<ListStoryItem>) {
        this.dataInformation.clear()
        this.dataInformation.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder =
        StoryViewHolder(ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = dataInformation[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            clickListener(item)
        }
    }

    override fun getItemCount(): Int = dataInformation.size



    class StoryViewHolder(private val binding: ListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            // Set image using Coil
            binding.imageView.load(item.photoUrl)

            // Set text
            binding.txtJudul.text = item.name
            binding.txtDeskripsi.text = item.description
        }
    }


}