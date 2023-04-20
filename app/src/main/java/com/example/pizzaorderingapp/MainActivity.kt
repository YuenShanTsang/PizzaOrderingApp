package com.example.pizzaorderingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.pizzaorderingapp.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityMainBinding

    // Shared preferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get shared preferences instance for orders
        sharedPreferences = getSharedPreferences("orders", Context.MODE_PRIVATE)

        // Set up topping list view
        val adapter = ToppingAdapter(this)
        binding.toppingListView.adapter = adapter

        // Set up submit button click listener
        binding.submitButton.setOnClickListener {

            val selectedToppings = adapter.getSelectedToppings()

            if (selectedToppings.isEmpty()) {
                // If the list is empty, display a  message indicating user needs to select at least one topping
                Toast.makeText(this, "Please select at least one topping", Toast.LENGTH_SHORT)
                    .show()
            } else {
                // Join selected toppings into a comma-separated string
                selectedToppings.joinToString(", ")
                // Show success message
                Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show()

                // Save the order in shared preferences
                val ordersJsonStr = sharedPreferences.getString("orders", "[]")
                val orders =
                    Gson().fromJson(ordersJsonStr, Array<Order>::class.java).toMutableList()
                orders.add(Order(selectedToppings))
                val newOrdersJsonStr = Gson().toJson(orders)
                sharedPreferences.edit().putString("orders", newOrdersJsonStr).apply()

                // Go to past orders activity and pass selected toppings as extra
                val intent = Intent(this, PastOrder::class.java)
                intent.putStringArrayListExtra("selectedToppings", ArrayList(selectedToppings))
                startActivity(intent)
            }
        }
    }


    // Add night switch in menu of the action bar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val item = menu.findItem(R.id.night_switch)
        val switchView = item.actionView as Switch

        // Set the switch state based on the current night mode
        switchView.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // Set a listener for changes to the switch state
        switchView.setOnCheckedChangeListener { _, isChecked ->
            val mode =
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        return true
    }

    // Add main menu in action bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.order_item -> {
                // Go to past order activity
                val intent = Intent(this, PastOrder::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
// Topping items (Name and Price)
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

// Adapter for the list of toppings
class ToppingAdapter(private val context: Context) : BaseAdapter() {

    // A mutable list to keep track of selected toppings
    private val selectedToppings = mutableListOf<String>()

    // A variable to keep track of the total price of selected toppings
    private var totalPrice = 0.0

    override fun getCount(): Int {
        // Return the total number of toppings available
        return Topping.values().size
    }

    override fun getItem(position: Int): Any {
        // Return the topping at the specified position
        return Topping.values()[position]
    }

    override fun getItemId(position: Int): Long {
        // Return the id of the item at the specified position
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.topping_item, parent, false)

        // Find the price text view in the main activity
        val priceText: TextView = (context as MainActivity).findViewById(R.id.price_text_view)

        // Find the checked text view for a topping item
        val toppingName = view.findViewById<CheckedTextView>(R.id.topping_name)

        // Get the topping object at the specified position
        val topping = getItem(position) as Topping

        // Set the text and checked state of the topping item
        toppingName.text = "${topping.toppings} - $${topping.price}"
        toppingName.isChecked = selectedToppings.contains(topping.toppings)

        // Set an onclick listener for the topping item view
        view.setOnClickListener {

            // Toggle the checked state of the topping item
            val isChecked = !selectedToppings.contains(topping.toppings)

            // Updates the selected toppings and total price based on the checked state
            if (isChecked) {
                selectedToppings.add(topping.toppings)
                totalPrice += topping.price
            } else {
                selectedToppings.remove(topping.toppings)
                totalPrice -= topping.price
            }

            // Updates the checked state and total price in the UI
            toppingName.isChecked = isChecked
            priceText.text = String.format("$%.2f", totalPrice)

        }

        return view
    }

    // Returns a list of selected toppings
    fun getSelectedToppings(): List<String> {
        return selectedToppings
    }

}