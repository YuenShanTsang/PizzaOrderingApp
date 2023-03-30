package com.example.pizzaorderingapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzaorderingapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityMainBinding

    // List to store selected toppings
    private val selectedToppings = mutableListOf<Topping>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the RecyclerView with the ToppingAdapter and the list of Topping enum values
        val toppings = Topping.values().toList()
        binding.toppingListRecyclerView.adapter = ToppingAdapter(toppings)
        binding.toppingListRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.submitButton.setOnClickListener {
            // Iterate over the toppings in the adapter and add the selected ones to the list
            val adapter = binding.toppingListRecyclerView.adapter as ToppingAdapter
            val selectedToppings = mutableListOf<Topping>()
            for (i in 0 until adapter.itemCount) {
                val holder = binding.toppingListRecyclerView.findViewHolderForAdapterPosition(i) as? ToppingAdapter.ViewHolder
                if (holder != null && holder.toppingName.isChecked) {
                    selectedToppings.add(adapter.toppings[i])
                }
            }

            if (selectedToppings.isNotEmpty()) {
                // Create the order object
                val order = Order(selectedToppings)

                // Process the order
                OrderProcessor.processOrder(this, order)

                // Write the selected toppings to a file
                val file = File(this.filesDir, "selected_toppings.txt")
                file.writeText(selectedToppings.joinToString(",") { it.toppings })


                // Clear the selected toppings list and show a toast message
                selectedToppings.clear()
                (binding.toppingListRecyclerView.adapter as? ToppingAdapter)?.notifyDataSetChanged()
                Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_SHORT).show()

                // Start the PastOrder activity with the selected toppings
                val intent = Intent(this, PastOrder::class.java)
                intent.putExtra("selectedToppings", selectedToppings.map { it.toppings }.toTypedArray())
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select at least one topping!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}




enum class Topping(val toppings: String) {
    Pepperoni("Pepperoni"),
    Mushroom("Mushroom"),
    ExtraCheese("Extra Cheese"),
    Sausage("Sausage"),
    Onion("Onion"),
    BlackOlives("Black Olives"),
    GreenPepper("Green Pepper"),
    FreshGarlic("Fresh Garlic"),
    Tomato("Tomato"),
    FreshBasil("Fresh Basil")
}



class ToppingAdapter(internal val toppings: List<Topping>) : RecyclerView.Adapter<ToppingAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val toppingName: CheckedTextView = view.findViewById(R.id.topping_name)

        init {
            // Add OnClickListener to toggle the checkbox state
            toppingName.setOnClickListener {
                toppingName.isChecked = !toppingName.isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.topping_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topping = toppings[position]
        holder.toppingName.text = topping.toppings
    }

    override fun getItemCount(): Int {
        return toppings.size
    }
}