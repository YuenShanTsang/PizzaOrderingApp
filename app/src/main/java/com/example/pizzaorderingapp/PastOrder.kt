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
        val file = File(this.filesDir, "selected_toppings.txt")
        if (file.exists()) {
            val selectedToppings = file.readText().split(",")

            // Create a new adapter for the RecyclerView
            val recyclerViewAdapter = MyRecyclerViewAdapter(selectedToppings)

            // Set the adapter for the RecyclerView
            binding.selectedToppingsRecyclerView.adapter = recyclerViewAdapter
        } else {
            file.createNewFile()
            binding.selectedToppingsRecyclerView.adapter = null
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
            // Handle delete button click
        }
    }

}


class Order(val toppings: List<Topping>)

object OrderProcessor {
    private const val FILENAME = "orders.txt"

    private fun saveOrders(context: Context, orders: List<Order>) {
        val file = File(getOrdersFilepath(context))
        val ordersJson = orders.map { Gson().toJson(it) }
        file.writeText(ordersJson.joinToString("\n"))
    }

    fun processOrder(context: Context, order: Order) {
        val orders = loadOrders(context)
        orders.add(order)
        saveOrders(context, orders)
    }

    private fun loadOrders(context: Context): MutableList<Order> {
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
        return File(context.filesDir, FILENAME).absolutePath
    }

}

class MyRecyclerViewAdapter(private val items: List<String>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.past_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.past_order_item_checkbox)
    }
}


