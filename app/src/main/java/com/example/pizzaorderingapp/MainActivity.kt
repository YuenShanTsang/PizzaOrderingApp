package com.example.pizzaorderingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzaorderingapp.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityMainBinding

    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up topping list view
        val adapter = ToppingAdapter(this)
        binding.toppingListView.adapter = adapter

        // Set up submit button click listener
        binding.submitButton.setOnClickListener {
            val selectedToppings = adapter.getSelectedToppings()
            val selectedToppingsString = selectedToppings.joinToString(", ")
            Toast.makeText(this, "Selected toppings: $selectedToppingsString", Toast.LENGTH_SHORT).show()

            // Save the order in shared preferences
            val ordersJson = sharedPreferences.getString("orders", "[]")
            val orders = Gson().fromJson(ordersJson, Array<PastOrder.Order>::class.java).toMutableList()
            orders.add(PastOrder.Order(selectedToppings))
            val newOrdersJson = Gson().toJson(orders)
            sharedPreferences.edit().putString("orders", newOrdersJson).apply()

            val intent = Intent(this, PastOrder::class.java)
            intent.putStringArrayListExtra("selectedToppings", ArrayList(selectedToppings))
            startActivity(intent)
        }

        sharedPreferences = getSharedPreferences("orders", Context.MODE_PRIVATE)
    }

    // Topping items
    enum class Topping(val toppings: String) {
        Pepperoni("Pepperoni"),
        Mushroom("Mushroom"),
        ExtraCheese("Extra Cheese"),
        Sausage("Sausage"),
        Onion("Onion"),
        Bacon("Bacon"),
        Ham("Ham"),
        Pineapple("Pineapple"),
        GreenPepper("Green Pepper"),
        BlackOlives("Black Olives")
    }

    class ToppingAdapter(private val context: Context) : BaseAdapter() {

        private val selectedToppings = mutableListOf<String>()

        override fun getCount(): Int {
            return Topping.values().size
        }

        override fun getItem(position: Int): Any {
            return Topping.values()[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.topping_item, parent, false)

            val toppingName = view.findViewById<CheckedTextView>(R.id.topping_name)
            val topping = getItem(position) as Topping
            toppingName.text = topping.toppings
            toppingName.isChecked = selectedToppings.contains(topping.toppings)

            view.setOnClickListener {
                val isChecked = !selectedToppings.contains(topping.toppings)
                if (isChecked) {
                    selectedToppings.add(topping.toppings)
                } else {
                    selectedToppings.remove(topping.toppings)
                }
                toppingName.isChecked = isChecked
            }

            return view
        }

        fun getSelectedToppings(): List<String> {
            return selectedToppings
        }
    }
}

