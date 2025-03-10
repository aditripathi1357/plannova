package com.example.plannova.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.plannova.R

class CategoryAdapter(private val context: Context, private val categories: List<String>, private val icons: List<Int>)
    : ArrayAdapter<String>(context, R.layout.category_item, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = convertView ?: inflater.inflate(R.layout.category_item, parent, false)

        val icon = view.findViewById<ImageView>(R.id.categoryIcon)
        val text = view.findViewById<TextView>(R.id.categoryText)

        icon.setImageResource(icons[position])
        text.text = categories[position]

        return view
    }
}
