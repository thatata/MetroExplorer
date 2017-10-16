package tarbi.metroexplorer.util

import android.support.v7.util.SortedList
import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tarbi.metroexplorer.R
import tarbi.metroexplorer.activity.LandmarksActivity

class MetroStationsAdapter(stations : List<Station>?, private val mActivity: Activity) :
        RecyclerView.Adapter<MetroStationsAdapter.ViewHolder>() {

    private lateinit var holder: ViewHolder

    private val mCallBack: SortedList.Callback<Station> = object :
            SortedList.Callback<Station>() {

        override fun areContentsTheSame(oldItem: Station?,
                                        newItem: Station?): Boolean {
            if (oldItem == null || newItem == null) return false
            return oldItem.equals(newItem)
        }

        override fun compare(o1: Station?, o2: Station?): Int {
            if (o1 == null || o2 == null) return -1
            return o1.compareTo(o2, false)
        }

        override fun areItemsTheSame(item1: Station?,
                                     item2: Station?): Boolean {
            return areContentsTheSame(item1, item2)
        }

        override fun onChanged(position: Int, count: Int) {
            Log.d("MyTag", "!!!onChanged Called")
            notifyItemRangeChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            Log.d("MyTag", "!!!onInserted Called, position: $position, count: $count")
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            Log.d("MyTag", "!!!onRemoved Called, position: $position, count: $count")
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            Log.d("MyTag", "!!!onMoved Called")
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    private val stationList: SortedList<Station> = SortedList<Station>(Station::class.java, mCallBack)

    init {
        stationList.addAll(stations)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val station = stationList.get(position)

        station?.let {
            (holder as ViewHolder).bind(station)
        }
    }

    override fun getItemCount(): Int {
        return stationList.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)

        holder = ViewHolder(layoutInflater.inflate(R.layout.row_station, parent, false))
        return holder
    }

    fun replaceAll(newStations: List<Station>) {
        stationList.beginBatchedUpdates()
        var i = stationList.size() - 1
        while (i >= 0) {
            val oldStation = stationList.get(i)
            if (!newStations.contains(oldStation))
                stationList.remove(oldStation)
            i--
        }
        stationList.addAll(newStations)
        stationList.endBatchedUpdates()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val myView : View = view
        private val stationTextView: TextView = view.findViewById(R.id.stationNameForView)

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
