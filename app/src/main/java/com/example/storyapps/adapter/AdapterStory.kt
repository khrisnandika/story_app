package com.example.storyapps.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.storyapps.data.database.StoryEntity
import com.example.storyapps.databinding.ListStoryBinding
import com.example.storyapps.databinding.LoadStateItemBinding

class AdapterStory(
    private val clickListener: (StoryEntity) -> Unit
) : PagingDataAdapter<StoryEntity, AdapterStory.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            holder.itemView.setOnClickListener {
                clickListener(item)
            }
        }
    }

    class StoryViewHolder(private val binding: ListStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: StoryEntity) {
            binding.imageView.load(item.photoUrl)
            binding.txtJudul.text = item.name
            binding.txtDeskripsi.text = item.description
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class LoadStateViewHolder(private val binding: LoadStateItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.txtError.isVisible = loadState is LoadState.Error
        binding.txtError.text = (loadState as? LoadState.Error)?.error?.localizedMessage
        binding.txtInfo.isVisible = loadState !is LoadState.Loading && loadState !is LoadState.Error
        binding.txtInfo.text = "Tidak ada data yang tersedia"
    }
}

class LoadStateAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
        holder.itemView.setOnClickListener { retry.invoke() }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LoadStateItemBinding.inflate(inflater, parent, false)
        return LoadStateViewHolder(binding)
    }
}
