package com.example.rtdatabasechat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(private val mDataSet: List<Chat>, private val mId: String) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = if (viewType == CHAT_END) {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_end, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.chat_start, parent, false)
        }
        return ViewHolder(v)
    }

    override fun getItemViewType(position: Int): Int {
        return if (mDataSet[position].id.equals(mId)) {
            CHAT_END
        } else CHAT_START
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = mDataSet[position]
        holder.mTextView.text = chat.message
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }
    class ViewHolder(v: View?) : RecyclerView.ViewHolder(v!!) {
        var mTextView: TextView = itemView.findViewById(R.id.tvMessage)

    }

    companion object {
        private const val CHAT_END = 1
        private const val CHAT_START = 2
    }
}
