package com.example.plannova.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plannova.Models.Venue
import com.example.plannova.R

class VenueAdapter(private val venues: List<Venue>) : RecyclerView.Adapter<VenueAdapter.VenueViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VenueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event_card, parent, false)
        return VenueViewHolder(view)
    }

    override fun onBindViewHolder(holder: VenueViewHolder, position: Int) {
        val venue = venues[position]
        holder.bind(venue)
    }

    override fun getItemCount(): Int {
        return venues.size
    }

    class VenueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        private val eventName: TextView = itemView.findViewById(R.id.eventName)
        private val eventDate: TextView = itemView.findViewById(R.id.eventDate)
        private val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        private val eventPrice: TextView = itemView.findViewById(R.id.eventPrice)

        fun bind(venue: Venue) {
            eventName.text = venue.venueName
            eventDate.text = venue.dateTime
            eventLocation.text = venue.venueLocation
            eventPrice.text = venue.pricing

            // Load image using Glide
            if (venue.imageUrls.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(venue.imageUrls[0])
                    .into(eventImage)
            }
        }
    }
}