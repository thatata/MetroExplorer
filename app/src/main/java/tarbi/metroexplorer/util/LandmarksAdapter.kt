package tarbi.metroexplorer.util

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import tarbi.metroexplorer.R
import tarbi.metroexplorer.activity.LandmarkDetailActivity

class LandmarksAdapter(private val landmarks : List<Landmark>?, private val mActivity: Activity) :
        RecyclerView.Adapter<LandmarksAdapter.ViewHolder>() {
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
        private val myView : View = view
        private val landmarkTextView: TextView = view.findViewById(R.id.landmarkNameForView)

        //update score row ui with score and date
        fun bind(landmark: Landmark) {
            landmarkTextView.text = landmark.name

            // set on click listener to launch LandmarkDetailActivity
            myView.setOnClickListener {
                // create intent
                val intent = Intent(mActivity, LandmarkDetailActivity::class.java)

                // attach landmark to intent
                intent.putExtra("landmark", landmark)

                mActivity.startActivity(intent)
            }
        }
    }
}