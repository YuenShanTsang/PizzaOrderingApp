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
import android.widget.TextView
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

        sharedPreferences = getSharedPreferences("orders", Context.MODE_PRIVATE)

        // Set up topping list view
        val adapter = ToppingAdapter(this)
        binding.toppingListView.adapter = adapter

        // Set up submit button click listener
        binding.submitButton.setOnClickListener {
            val selectedToppings = adapter.getSelectedToppings()
            if (selectedToppings.isEmpty()) {
                Toast.makeText(this, "Please select at least one topping", Toast.LENGTH_SHORT).show()
            } else {
                selectedToppings.joinToString(", ")
                Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show()

                // Save the order in shared preferences
                val ordersJsonStr = sharedPreferences.getString("orders", "[]")
                val orders = Gson().fromJson(ordersJsonStr, Array<Order>::class.java).toMutableList()
                orders.add(Order(selectedToppings))
                val newOrdersJsonStr = Gson().toJson(orders)
                sharedPreferences.edit().putString("orders", newOrdersJsonStr).apply()

                val intent = Intent(this, PastOrder::class.java)
                intent.putStringArrayListExtra("selectedToppings", ArrayList(selectedToppings))
                startActivity(intent)
            }
        }
    }

}

// Topping items
enum class Topping(val toppings: String, val price: Double) {
    Pepperoni("Pepperoni", 0.99),
    Mushroom("Mushroom", 0.79),
    Tomato("Tomato", 1.29),
    Sausage("Sausage", 1.49),
    Onion("Onion", 0.69),
    Bacon("Bacon", 1.79),
    Ham("Ham", 1.29),
    Pineapple("Pineapple", 0.89),
    Spinach("Spinach", 0.69),
    Olives("Olives", 0.99)
}
class ToppingAdapter(private val context: Context) : BaseAdapter() {

    private val selectedToppings = mutableListOf<String>()

    private var totalPrice = 0.0

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

        val priceText: TextView = (context as MainActivity).findViewById(R.id.price_text_view)

        val toppingName = view.findViewById<CheckedTextView>(R.id.topping_name)
        val topping = getItem(position) as Topping
        toppingName.text = "${topping.toppings} - $${topping.price}"
        toppingName.isChecked = selectedToppings.contains(topping.toppings)

        view.setOnClickListener {
            val isChecked = !selectedToppings.contains(topping.toppings)
            if (isChecked) {
                selectedToppings.add(topping.toppings)
                totalPrice += topping.price
            } else {
                selectedToppings.remove(topping.toppings)
                totalPrice -= topping.price
            }
            toppingName.isChecked = isChecked
            priceText.text = String.format("$%.2f", totalPrice)

        }

        return view
    }

    fun getSelectedToppings(): List<String> {
        return selectedToppings
    }

}