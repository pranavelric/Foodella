package com.food.foodella.fragments

import android.content.Context
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.food.foodella.R
import com.food.foodella.adapter.FaqAdapter
import com.food.foodella.modal.FaqModal
import kotlinx.android.synthetic.main.fragment_faq.view.*


class FaqFragment(val contextParam: Context) : Fragment() {

    lateinit var vieww: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vieww = inflater.inflate(R.layout.fragment_faq, container, false)


        var faq_items = ArrayList<FaqModal>()

        faq_items.add(
            FaqModal(
                "Q1. What is Foodella doing this?",
                "At Food Runner we strive to add value to your business. We understand the challenges you face on a daily basis in sourcing the best quality packaging material. Food Runner Packaging Assist is to take care of all your packaging needs"
            )
        )

        faq_items.add(
            FaqModal(

                "Q2. How are you guys are different?",
                "Best products-We only list the products which have passed our tests and are the best suitable for delivery. They will help in giving a better consumer experience, so you can get get repeated orders. Best prices-We have negotiated rates with our distributors exclusive for Food Runner Restaurant. Convenience-No need to haggle for prices. No need to follow-up on delivery. We track all orders to ensure you have a hassle free experience."
                  )

        )
        faq_items.add(
            FaqModal(

                "Q3. Who will receive payment of orders?",
                "Restaurant owner will get whole payment of orders placed through Foodchow. They have to set their own PayPal Account for online transaction."
            )

        )
        faq_items.add(
            FaqModal(

                "Q4. How much time does it take to deliver order?",
                "Generally it takes between 45 minutes to 1 hour time to deliver the order. Due to long distance or heavy traffic, delivery might take few extra minutes."

            )

        )
        faq_items.add(
            FaqModal(

                "Q5. Do you offer Cash on Delivery?",
                "Yes. We offer Cash on Delivery which most of the other apps don't offer."
            )

        )





        vieww.recycler_view_faq.adapter = FaqAdapter(contextParam, faq_items)
        vieww.recycler_view_faq.layoutManager = LinearLayoutManager(contextParam)

        return vieww
    }

}