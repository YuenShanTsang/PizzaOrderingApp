package com.example.pizzaorderingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzaorderingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the RecyclerView with the ToppingAdapter and the list of Topping enum values
        val toppings = Topping.values().toList()
        binding.todoListRecyclerView.adapter = ToppingAdapter(toppings)
        binding.todoListRecyclerView.layoutManager = LinearLayoutManager(this)


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



class ToppingAdapter(private val toppings: List<Topping>) : RecyclerView.Adapter<ToppingAdapter.ViewHolder>() {

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


