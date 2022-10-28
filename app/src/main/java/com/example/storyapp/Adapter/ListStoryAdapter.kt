package com.example.storyapp.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.Helper.MapLocationConvert
import com.example.storyapp.Model.ListStoryUserData
import com.example.storyapp.R
import com.example.storyapp.UI.DetailStoryActivity
import com.example.storyapp.databinding.ItemListStoryBinding

class ListStoryAdapter(val context: Context) :
    PagingDataAdapter<ListStoryUserData, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ListViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)

            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClicked(data)

                val adapter = Intent(it.context, DetailStoryActivity::class.java)
                adapter.putExtra(DetailStoryActivity.LIST_EXTRA_STORIES, data)
                it.context.startActivity(adapter,
                    ActivityOptionsCompat
                        .makeSceneTransitionAnimation(it.context as Activity)
                        .toBundle()
                )
            }

        }
    }

    class ListViewHolder(private var storyBinding: ItemListStoryBinding, val context: Context) :
        RecyclerView.ViewHolder(storyBinding.root) {
        fun bind(data: ListStoryUserData) {
            storyBinding.tvName.text = data.name
            storyBinding.tvLocation.text = MapLocationConvert.getMapLocation(
                MapLocationConvert.toLatLon(data.lat, data.lon),
                context
            )
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(storyBinding.ivItemPhoto)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryUserData)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryUserData>() {
            override fun areItemsTheSame(
                oldItem: ListStoryUserData,
                newItem: ListStoryUserData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryUserData,
                newItem: ListStoryUserData
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }


}