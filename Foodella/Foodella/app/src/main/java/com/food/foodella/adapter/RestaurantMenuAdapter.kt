package com.food.foodella.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.food.foodella.R
import com.food.foodella.activities.CartActivity
import com.food.foodella.modal.RestaurantMenu

class RestaurantMenuAdapter(
    val context: Context,
    val resId: String,
    val resName: String,
    val buttonProceedToCart: TextView,
    val LayoutGoToCart: ConstraintLayout,
    val restaurantMenu: ArrayList<RestaurantMenu>
) : RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolderRestaurantMenu>() {


    var itemSelectedCount: Int = 0
    var itemsSelectedId = arrayListOf<String>()


    class ViewHolderRestaurantMenu(view: View) : RecyclerView.ViewHolder(view) {
        val textViewSerialNumber: TextView = view.findViewById(R.id.textViewSerialNumber)
        val textViewItemName: TextView = view.findViewById(R.id.textViewItemName)
        val textViewItemPrice: TextView = view.findViewById(R.id.textViewItemPrice)
        val buttonAddToCart: Button = view.findViewById(R.id.buttonAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRestaurantMenu {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.food_item_row, parent, false)

        return ViewHolderRestaurantMenu(view)
    }

    override fun getItemCount(): Int {
        return restaurantMenu.size
    }

    override fun onBindViewHolder(holder: ViewHolderRestaurantMenu, position: Int) {


        buttonProceedToCart.setOnClickListener(View.OnClickListener {

            val options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out)

            val intent = Intent(context, CartActivity::class.java)

            intent.putExtra("restaurantId", resId)
            intent.putExtra("restaurantName", resName)
            intent.putExtra("selectedItemsId", itemsSelectedId)

            context.startActivity(intent,options.toBundle())

        })

        holder.buttonAddToCart.setOnClickListener {

            if (holder.buttonAddToCart.text.toString() == "Remove") {
                itemSelectedCount--

                itemsSelectedId.remove(holder.buttonAddToCart.tag.toString())

                holder.buttonAddToCart.text = "Add"



                holder.buttonAddToCart.background.setTintList(
                    ContextCompat.getColorStateList(
                        context,
                        R.color.colorPrimary
                    )
                )
            } else {
                itemSelectedCount++//selected

                itemsSelectedId.add(holder.buttonAddToCart.tag.toString())


                holder.buttonAddToCart.text = "Remove"


                holder.buttonAddToCart.background.setTintList(
                    ContextCompat.getColorStateList(
                        context,
                        android.R.color.holo_orange_light
                    )
                )
            }

            if (itemSelectedCount > 0) {
                LayoutGoToCart.visibility = View.VISIBLE
            } else {
                LayoutGoToCart.visibility = View.INVISIBLE
            }


        }


        holder.buttonAddToCart.tag = restaurantMenu[position].id + ""//save the item id in textViewName Tag ,will be used to add to cart
        holder.textViewSerialNumber.text = (position + 1).toString()//position starts from 0
        holder.textViewItemName.text = restaurantMenu[position].name
        holder.textViewItemPrice.text = "Rs." + restaurantMenu[position].cost_for_one


    }

    fun getSelectedItemCount(): Int {
        return itemSelectedCount
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}