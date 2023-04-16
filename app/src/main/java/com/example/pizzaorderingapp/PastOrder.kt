package com.example.pizzaorderingapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.pizzaorderingapp.databinding.ActivityPastOrderBinding

class PastOrder : AppCompatActivity() {

    // View binding
    private lateinit var binding: ActivityPastOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setup view binding
        binding = ActivityPastOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val selectedToppings = intent.getStringArrayListExtra("selectedToppings")

        // Display the selected toppings
        binding.requestedTextView.text = selectedToppings?.joinToString()

        // Concatenate the selected toppings into a single string
        val toppingsString = selectedToppings?.joinToString()

        // Create an adapter to display the selected toppings in the ListView
        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_multiple_choice,
            listOf(toppingsString)
        )

        // Set the adapter on the ListView
        binding.selectedToppingsListView.adapter = adapter

        // Enable multiple item selection on the ListView
        binding.selectedToppingsListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

}
