package com.food.foodella.adapter

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.food.foodella.R
import com.food.foodella.activities.RestaurantMenuActivity
import com.food.foodella.database.RestaurantDatabase
import com.food.foodella.database.RestaurantEntity
import com.food.foodella.modal.Restaurant
import com.squareup.picasso.Picasso
import kotlin.random.Random

class BaseHomeAdapter(val context: Context, var itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<BaseHomeAdapter.ViewHolderBaseHome>() {

    class ViewHolderBaseHome(view: View) : RecyclerView.ViewHolder(view) {


        val imageViewRestaurant: ImageView = view.findViewById(R.id.imageViewRestaurant)
        val textViewRestaurantName: TextView = view.findViewById(R.id.res_name)
        val textViewPricePerPerson: TextView = view.findViewById(R.id.total_price)
        val textViewRating: TextView = view.findViewById(R.id.textViewRating)
        val textViewfavourite: TextView = view.findViewById(R.id.textViewfavourite)
        val baseHomeItemCard: CardView = view.findViewById(R.id.base_home_res_card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBaseHome {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.basehome_recycler_item, parent, false)

        return ViewHolderBaseHome(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderBaseHome, position: Int) {


        val currentRestaurant = itemList[position]


        val restaurantEntity = RestaurantEntity(
            currentRestaurant.restaurantId,
            currentRestaurant.restaurantName
        )




        holder.textViewfavourite.setOnClickListener {
            if (!DatabaseAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val result = DatabaseAsyncTask(context, restaurantEntity, 2).execute().get()
                if (result) {

                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()

                    holder.textViewfavourite.setTag("liked")//new value
                    holder.textViewfavourite.background =
                        context.resources.getDrawable(R.drawable.ic_fav_red)

                } else {
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
                }

            } else {


                val result = DatabaseAsyncTask(context, restaurantEntity, 3).execute().get()

                if (result) {

                    Toast.makeText(context, "Removed favourites", Toast.LENGTH_SHORT).show()

                    holder.textViewfavourite.setTag("unliked")
                    holder.textViewfavourite.background =
                        context.resources.getDrawable(R.drawable.ic_fav_outline)
                } else {

                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()

                }


            }

        }


        holder.baseHomeItemCard.setOnClickListener {

            val options = ActivityOptions.makeCustomAnimation(
                context,
                android.R.anim.fade_in, android.R.anim.fade_out
            )
val intent =  Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("restaurantId",currentRestaurant.restaurantId)
            intent.putExtra("restaurantName",currentRestaurant.restaurantName)
            intent.putExtra("restaurantRating",currentRestaurant.restaurantRating)
            intent.putExtra("restaurantPrice",currentRestaurant.cost_for_one)
            intent.putExtra("restaurantImage",currentRestaurant.restaurantImage)

            context.startActivity(
              intent,
                options.toBundle()
            )


        }

        holder.textViewRestaurantName.text = currentRestaurant.restaurantName
        holder.textViewPricePerPerson.text = currentRestaurant.cost_for_one
        holder.textViewRating.text = currentRestaurant.restaurantRating

        Picasso.get().load(currentRestaurant.restaurantImage).error(R.drawable.food)
            .into(holder.imageViewRestaurant)


        val isFav = DatabaseAsyncTask(context, restaurantEntity, 1).execute().get()
        if (isFav) {
            holder.textViewfavourite.setTag("liked")
            holder.textViewfavourite.background =
                context.resources.getDrawable(R.drawable.ic_fav_red)

        } else {
            holder.textViewfavourite.setTag("unliked")
            holder.textViewfavourite.background =
                context.resources.getDrawable(R.drawable.ic_fav_outline)
        }


    }


    fun filterList(filteredList: ArrayList<Restaurant>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class DatabaseAsyncTask(
        val context: Context,
        val restaurantEntity: RestaurantEntity,
        val mode: Int
    ) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {


            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> return false

            }
        }

    }


}