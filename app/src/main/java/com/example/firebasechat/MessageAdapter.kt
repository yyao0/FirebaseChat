package com.example.firebasechat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasechat.databinding.ItemMessageBinding

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class ViewHolder(private val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            with(binding) {
                tvUsername.text = "User: ${message.username.toString()}"
                tvMessage.text = message.text.toString()

                if (!message.imageUrl.isNullOrEmpty()) {
                    tvMessage.visibility = View.GONE
                    imageView.visibility = View.VISIBLE
                    Glide.with(imageView.context)
                        .load(message.imageUrl)
                        .into(imageView)
                } else {
                    tvMessage.visibility = View.VISIBLE
                    imageView.visibility = View.GONE
                }
            }
        }
    }
}