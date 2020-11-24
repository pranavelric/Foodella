package com.food.foodella.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.food.foodella.R
import com.food.foodella.modal.FaqModal

class FaqAdapter(val context: Context, val items: ArrayList<FaqModal>) :
    RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    class FaqViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val Question: TextView = view.findViewById(R.id.textView_question)
        val answer: TextView = view.findViewById(R.id.textView_answer)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqViewHolder {
        val vieww = LayoutInflater.from(context).inflate(R.layout.faq_item, parent, false)
        return FaqViewHolder(vieww)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {

        holder.Question.text = items[position].question
        holder.answer.text = items[position].answer


    }


}