package com.food.foodella.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.food.foodella.R
import com.food.foodella.modal.CartItem

class CartAdapter(val context: Context,val cartItem:ArrayList<CartItem>):RecyclerView.Adapter<CartAdapter.ViewHolderCart>(){

    class ViewHolderCart(view:View):RecyclerView.ViewHolder(view){

        val textViewOrderItem: TextView =view.findViewById(R.id.textViewOrderItem)
        val textViewOrderItemPrice: TextView =view.findViewById(R.id.textViewOrderItemPrice)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ViewHolderCart {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.cart_item,parent,false)

        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItem.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject=cartItem[position]


        holder.textViewOrderItem.text=cartItemObject.itemName
        holder.textViewOrderItemPrice.text="Rs. "+cartItemObject.itemPrice
    }


}