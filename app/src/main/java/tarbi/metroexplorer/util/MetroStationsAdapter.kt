package tarbi.metroexplorer.util

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tarbi.metroexplorer.R
import tarbi.metroexplorer.activity.LandmarksActivity

class MetroStationsAdapter(private val stations : List<Station>?, private val mActivity: Activity) :
        RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        //obtain station at position
        val station = stations?.get(position)

        //bind score to view holder
        station?.let {
            (holder as ViewHolder).bind(station)
        }
    }

    override fun getItemCount(): Int {
        if (stations == null) return -1
        return stations.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)

        //inflate our score row layout
        return ViewHolder(layoutInflater.inflate(R.layout.row_station, parent, false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val myView : View = view
        private val stationTextView: TextView = view.findViewById(R.id.stationNameForView)

        //update station row ui with station name
        fun bind(station: Station) {
            stationTextView.text = station.stationName

            // set on click listener that will trigger the LandmarksActivity
            myView.setOnClickListener {
                // create new intent
                val intent = Intent(mActivity, LandmarksActivity::class.java)

                // attach station to the intent
                intent.putExtra("station", station)
                mActivity.startActivity(intent)
            }

        }
    }
}