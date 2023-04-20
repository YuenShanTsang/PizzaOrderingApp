package com.example.pizzaorderingapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzaorderingapp.databinding.ActivityPastOrderBinding
import com.google.gson.Gson

class PastOrder : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityPastOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityPastOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the saved orders from shared preferences
        val ordersJson = getSharedPreferences("orders", Context.MODE_PRIVATE).getString("orders", "[]")
        val orders = Gson().fromJson(ordersJson, Array<Order>::class.java)

        // Create an adapter to display the orders in the ListView
        val adapter = OrderAdapter(this, orders.toList())

        // Set the adapter on the ListView
        binding.selectedToppingsListView.adapter = adapter
    }

    data class Order(val toppings: List<String>)

    class OrderAdapter(context: Context, orders: List<Order>) : ArrayAdapter<Order>(context, R.layout.past_order, orders) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.past_order, parent, false)

            val toppingsTextView = view.findViewById<CheckedTextView>(R.id.past_order_item_checkbox)

            val order = getItem(position)
            toppingsTextView.text = order?.toppings!!.joinToString(", ")

            return view
        }
    }
}
