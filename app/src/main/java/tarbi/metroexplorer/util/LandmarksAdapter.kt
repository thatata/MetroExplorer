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
class LandmarksAdapter(private val landmarks : List<Landmark>?) : RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        //obtain landmark at position
        val landmark = landmarks?.get(position)

        //bind score to view holder
        landmark?.let {
            (holder as ViewHolder).bind(landmark)
        }
    }

    override fun getItemCount(): Int {
        if (landmarks == null) return -1
        return landmarks.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)

        //inflate our score row layout
        return ViewHolder(layoutInflater.inflate(R.layout.row_landmark, parent, false))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val landmarkTextView: TextView = view.findViewById(R.id.landmarkNameForView)

        //update score row ui with score and date
        fun bind(landmark: Landmark) {
            landmarkTextView.text = landmark.name
        }
    }
}