package com.example.pizzaorderingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzaorderingapp.databinding.ActivityPastOrderBinding
import com.google.gson.Gson

class PastOrder : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityPastOrderBinding

    // List of orders retrieved from shared preferences
    private val orders: List<Order> by lazy {
        getSavedOrders()
    }

    // Selected order position
    private var selectedOrderPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityPastOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create an adapter to display the orders in the ListView
        val adapter = OrderAdapter(this, orders, selectedOrderPosition)

        // Set the adapter on the ListView
        binding.selectedToppingsListView.adapter = adapter

        binding.selectedToppingsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            orders.forEach { it.isChecked = false }
            orders[position].isChecked = true
            selectedOrderPosition = position
            adapter.notifyDataSetChanged()
        }

        binding.newOrderButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.deleteButton.setOnClickListener {
            if (selectedOrderPosition >= 0) {
                val updatedOrders = orders.toMutableList()
                updatedOrders.removeAt(selectedOrderPosition)
                updateSavedOrders(updatedOrders)
                adapter.clear()
                adapter.addAll(updatedOrders)
                adapter.notifyDataSetChanged()
                selectedOrderPosition = -1
            } else {
                Toast.makeText(this, "Please select an order to delete", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getSavedOrders(): List<Order> {
        val ordersJson =
            getSharedPreferences("orders", Context.MODE_PRIVATE).getString("orders", "[]")
        return Gson().fromJson(ordersJson, Array<Order>::class.java).toList()
    }

    private fun updateSavedOrders(orders: List<Order>) {
        val editor = getSharedPreferences("orders", Context.MODE_PRIVATE).edit()
        val ordersJson = Gson().toJson(orders)
        editor.putString("orders", ordersJson)
        editor.apply()
    }
}

data class Order(val toppings: List<String>, var isChecked: Boolean = false)

class OrderAdapter(
    context: Context,
    orders: List<Order>,
    private val selectedOrderPosition: Int
) : ArrayAdapter<Order>(context, R.layout.past_order, orders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.past_order, parent, false)

        val pastOrderCheckbox = view.findViewById<CheckedTextView>(R.id.past_order_item_checkbox)
        val pastOrder = getItem(position) as Order
        pastOrderCheckbox.text = pastOrder.toppings.joinToString(", ")
        pastOrderCheckbox.isChecked = position == selectedOrderPosition
        pastOrderCheckbox.isChecked = pastOrder.isChecked

        return view
    }
}