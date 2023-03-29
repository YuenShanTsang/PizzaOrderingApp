package com.example.pizzaorderingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.pizzaorderingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    enum class Topping{
        Pepperoni,
        Mushroom,
        Onion,
        Sausage,
        Tomato
    }

    // View binding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup spinner
        val toppings = Topping.values()
        val adapter = ArrayAdapter(this, R.layout.spinner_style, toppings)
        adapter.setDropDownViewResource(R.layout.dropdown_style)
        binding.toppingSpinner.adapter = adapter

        binding.toppingSpinner.post {
            // Get reference to ListView and set choice mode
            val listView = binding.toppingSpinner.listView
            listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        }

        binding.toppingSpinner.onItemSelectedListener = this


    }

    private fun handleSelectedItems(selectedItems: List<Topping>) {
        // Do something with the selected items, such as adding them to a list or displaying them on the screen
    }

    // Spinner functions
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selectedItems = mutableListOf<Topping>()
        for (i in 0 until parent.count) {
            if (parent.isItemChecked(i)) {
                selectedItems.add(parent.getItemAtPosition(i) as Topping)
            }
        }
        // Do something with the selected items
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // No action needed if nothing is selected
    }

}