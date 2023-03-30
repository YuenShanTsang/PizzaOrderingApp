package com.example.pizzaorderingapp

import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
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

        // Set the selected toppings in the ListView
        val file = File(this.filesDir, "selected_toppings.txt")
        if (file.exists()) {
            val selectedToppings = file.readText().split(",")
            val adapter = ArrayAdapter(this, R.layout.past_order, arrayOf(selectedToppings.joinToString()))
            binding.selectedToppingsListView.adapter = adapter
        } else {
            file.createNewFile()
            binding.selectedToppingsListView.adapter = null
        }

        // Set click listeners for the buttons
        binding.changeButton.setOnClickListener {
            // Handle change button click
        }

        binding.deleteButton.setOnClickListener {
            // Handle delete button click
        }
    }

}


class Order(val toppings: List<Topping>)

object OrderProcessor {
    private const val FILENAME = "orders.txt"

    fun createOrder(context: Context, order: Order) {
        val file = File(getOrdersFilepath(context))
        val orderJson = Gson().toJson(order)
        file.appendText(orderJson + "\n")
    }

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


