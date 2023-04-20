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

        // Set an item click listener on the ListView
        binding.selectedToppingsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // Update the isChecked state of each order
            orders.forEach { it.isChecked = false }
            orders[position].isChecked = true
            selectedOrderPosition = position
            adapter.notifyDataSetChanged()
        }

        // Set a click listener on the new order button
        binding.newOrderButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Set a click listener on the delete button
        binding.deleteButton.setOnClickListener {
            if (selectedOrderPosition >= 0) {
                // Remove the selected order from the list and update shared preferences
                val updatedOrders = orders.toMutableList()
                updatedOrders.removeAt(selectedOrderPosition)
                updateSavedOrders(updatedOrders)

                // Clear the adapter and add the updated orders, then notify the adapter of the changes
                adapter.clear()
                adapter.addAll(updatedOrders)
                adapter.notifyDataSetChanged()

                // Reset the selected order position and display a success message
                selectedOrderPosition = -1
                Toast.makeText(this, "Successfully deleted!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Display an error message if no order is selected for deletion
                Toast.makeText(this, "Please select an order to delete", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Retrieve the saved orders from shared preferences
    private fun getSavedOrders(): List<Order> {
        val ordersJson =
            getSharedPreferences("orders", Context.MODE_PRIVATE).getString("orders", "[]")
        return Gson().fromJson(ordersJson, Array<Order>::class.java).toList()
    }

    // Update the saved orders in shared preferences
    private fun updateSavedOrders(orders: List<Order>) {
        val editor = getSharedPreferences("orders", Context.MODE_PRIVATE).edit()
        val ordersJson = Gson().toJson(orders)
        editor.putString("orders", ordersJson)
        editor.apply()
    }
}


// A data class representing a past order
// Contain a list of toppings and a boolean flag to indicate if the order is checked or not
data class Order(val toppings: List<String>, var isChecked: Boolean = false)


// Adapter for the ListView displaying the list of past orders
class OrderAdapter(
    context: Context,
    orders: List<Order>,
    private val selectedOrderPosition: Int
) : ArrayAdapter<Order>(context, R.layout.past_order, orders) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.past_order, parent, false)

        // Get the checkbox for the past order item
        val pastOrderCheckbox = view.findViewById<CheckedTextView>(R.id.past_order_item_checkbox)

        // Get the Order object at the current position
        val pastOrder = getItem(position) as Order

        // Set the text of the checkbox to display the list of toppings for the past order
        pastOrderCheckbox.text = pastOrder.toppings.joinToString(", ")

        // Set the checkbox to be checked if the current position is the selected order position
        pastOrderCheckbox.isChecked = position == selectedOrderPosition

        // Set the checkbox to be checked if the order is checked
        pastOrderCheckbox.isChecked = pastOrder.isChecked

        return view
    }
}