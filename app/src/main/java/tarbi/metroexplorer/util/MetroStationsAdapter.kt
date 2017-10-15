package tarbi.metroexplorer.util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tarbi.metroexplorer.R

/**
 * Created by hobbes on 9/26/17.
 */
class MetroStationsAdapter(private val stations : List<Station>) : RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        //obtain score at position
        val station = stations?.get(position)

        //bind score to view holder
        station?.let {
            (holder as ViewHolder).bind(station)
        }
    }

    override fun getItemCount(): Int {
        return stations.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)

        //inflate our score row layout
        return ViewHolder(layoutInflater.inflate(R.layout.row_station, parent, false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val stationTextView: TextView = view.findViewById(R.id.stationNameForView)

        //update score row ui with score and date
        fun bind(station: Station) {
            stationTextView.text = station.name
        }
    }
}