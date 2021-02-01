package com.chooloo.www.callmanager.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import com.chooloo.www.callmanager.R
import com.chooloo.www.callmanager.adapter.CGroupsAdapter.CGroupHolder
import com.chooloo.www.callmanager.database.AppDatabase
import com.chooloo.www.callmanager.database.DataRepository
import com.chooloo.www.callmanager.database.entity.CGroupAndItsContacts
import com.chooloo.www.callmanager.listener.OnItemClickListener
import com.chooloo.www.callmanager.util.Utilities
import java.util.*

class CGroupsAdapter(private val mContext: Context,
                     private var mData: List<CGroupAndItsContacts>?,
                     private val mOnItemClickListener: OnItemClickListener?) : RecyclerView.Adapter<CGroupHolder>() {
    private val mRepository: DataRepository = DataRepository.getInstance(AppDatabase.getDatabase(mContext))
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CGroupHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false)
        return CGroupHolder(view)
    }

    override fun onBindViewHolder(holder: CGroupHolder, position: Int) {
        val cgroupAndItsContacts = mData!![position]
        val names: MutableList<String> = ArrayList()
        for (contact in cgroupAndItsContacts.contacts) {
            names.add(contact.name)
        }
        val namesStr = Utilities.joinStringsWithSeparator(names, ", ")

        // Set texts
        holder.name!!.text = cgroupAndItsContacts.cgroup.name
        holder.number!!.text = namesStr

        // Set onClick/LongClick listeners
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener { v: View? -> mOnItemClickListener.onItemClick(holder, cgroupAndItsContacts.cgroup) }
        }
        holder.itemView.setOnLongClickListener { v: View? ->
            //Create a dialog with options regarding the selected list
            MaterialDialog.Builder(mContext)
                    .title(R.string.dialog_long_click_cgroup_title)
                    .items(R.array.dialog_long_click_cgroup_options)
                    .itemsCallback { dialog: MaterialDialog?, itemView: View?, listPosition: Int, text: CharSequence? ->
                        when (listPosition) {
                            0 -> mRepository.deleteCGroup(cgroupAndItsContacts.cgroup.listId)
                        }
                    }
                    .show()
            notifyItemRemoved(holder.adapterPosition)
            true
        }
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    /**
     * Sets the data
     *
     * @param data
     */
    fun setData(data: List<CGroupAndItsContacts>?) {
        mData = data
        notifyDataSetChanged()
    }

    inner class CGroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.item_photo)
        var image: ImageView? = null

        @BindView(R.id.item_big_text)
        var name: TextView? = null

        @BindView(R.id.item_small_text)
        var number: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
        }
    }

}