package com.blogspot.alexeykutovenko.githubber.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.alexeykutovenko.githubber.R
import com.blogspot.alexeykutovenko.githubber.network.RepoItem
import com.blogspot.alexeykutovenko.githubber.ui.HomeFragment.OnFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_repoitem.view.*


class HomeAdapter(
    var mValues: List<RepoItem>,
    private val mListener: OnFragmentInteractionListener?
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as RepoItem
            mListener?.onFragmentInteraction(item.html_url)
        }
    }

    fun setValues(values: List<RepoItem>){
        this.mValues = values
        notifyItemRangeChanged(0, mValues.size)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_repoitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues.get(position)
        holder.mIdView.text = item.name
        holder.mContentView.text = item.html_url

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.repo_name
        val mContentView: TextView = mView.content

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
