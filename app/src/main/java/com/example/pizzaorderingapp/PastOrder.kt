package com.example.pizzaorderingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzaorderingapp.databinding.ActivityPastOrderBinding
import java.io.File
import com.google.gson.Gson

class PastOrder : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityPastOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityPastOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.selectedToppingsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Set the selected toppings in the RecyclerView
        val toppingFile = File(this.filesDir, "selected_toppings.txt")
        if (toppingFile.exists()) {
            val selectedToppings = toppingFile.readText().split(",")

            // Create a new adapter for the RecyclerView
            val recyclerViewAdapter = MyRecyclerViewAdapter(selectedToppings)

            // Set the adapter for the RecyclerView
            binding.selectedToppingsRecyclerView.adapter = recyclerViewAdapter
        } else {
            toppingFile.createNewFile()
            binding.selectedToppingsRecyclerView.adapter = null
        }

        // Display inputted text
        val inputText = intent.getStringExtra("inputText")
        if (inputText != null) {
            binding.requestedTextView.text = inputText
        }

        // Set click listeners for the buttons
        binding.changeButton.setOnClickListener {
            // Get the current selected toppings from the file
            val file = File(this.filesDir, "selected_toppings.txt")
            val selectedToppings = file.readText().split(",")

            // Launch the MainActivity with the current selected toppings as an intent extra
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("selectedToppings", selectedToppings.toTypedArray())
            startActivity(intent)
        }

        binding.deleteButton.setOnClickListener {
            // (Handle delete here)
        }
    }
}

// Data class for holding the selected toppings for an order
data class Order(val toppings: List<Topping>)

// Helper object for processing orders
object OrderProcessor {
    private const val FILENAME = "orders.txt"

    private fun saveOrders(context: Context, orders: List<Order>) {
        // Save orders as JSON strings in a text file
        val file = File(getOrdersFilepath(context))
        val ordersJson = orders.map { Gson().toJson(it) }
        file.writeText(ordersJson.joinToString("\n"))
    }

    fun processOrder(context: Context, order: Order) {
        // Load orders, add the new order, and save the updated orders
        val orders = loadOrders(context)
        orders.add(order)
        saveOrders(context, orders)
    }

    private fun loadOrders(context: Context): MutableList<Order> {
        // Load orders from the text file and parse as JSON strings
        val file = File(getOrdersFilepath(context))
        if (!file.exists()) {
            file.createNewFile()
        }
        val orderJsonList = file.readLines()
        val orders = mutableListOf<Order>()
        orderJsonList.forEach {
            val order = Gson().fromJson(it, Order::class.java)
            orders.add(order)
        }
        return orders
    }

    private fun getOrdersFilepath(context: Context): String {
        // Return the absolute path for the orders text file
        return File(context.filesDir, FILENAME).absolutePath
    }
}

// RecyclerView adapter for displaying the selected toppings in a past order
class MyRecyclerViewAdapter(private val items: List<String>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    // Create a new ViewHolder by inflating the past_order layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.past_order, parent, false)
        return ViewHolder(view)
    }

    // Bind data to the views in the ViewHolder for the given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    // Return the number of items in the data set
    override fun getItemCount(): Int {
        return items.size
    }

    // Define the ViewHolder, which holds references to views in the past_order layout
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.past_order_item_checkbox)
    }
}
