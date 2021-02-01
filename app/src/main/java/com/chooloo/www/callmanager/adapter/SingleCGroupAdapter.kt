package com.chooloo.www.callmanager.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import butterknife.BindView
import butterknife.ButterKnife
import com.chooloo.www.callmanager.R
import com.chooloo.www.callmanager.adapter.SingleCGroupAdapter.ContactHolder
import com.chooloo.www.callmanager.adapter.helper.ItemTouchHelperListener
import com.chooloo.www.callmanager.adapter.helper.ItemTouchHelperViewHolder
import com.chooloo.www.callmanager.adapter.helper.SimpleItemTouchHelperCallback.ItemTouchHelperAdapter
import com.chooloo.www.callmanager.database.AppDatabase
import com.chooloo.www.callmanager.database.DataRepository
import com.chooloo.www.callmanager.database.entity.Contact
import java.util.*
import kotlin.collections.ArrayList

class SingleCGroupAdapter(private val mContext: AppCompatActivity, private val mRecyclerView: RecyclerView, private val mItemTouchHelperListener: ItemTouchHelperListener?) : RecyclerView.Adapter<ContactHolder>(), ItemTouchHelperAdapter {
    private val mRepository: DataRepository
    private var mData: ArrayList<Contact?>? = null
    private var mEditModeEnabled = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_editable, parent, false)
        return ContactHolder(view)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val contact = mData!![position]
        holder.name!!.text = contact!!.name
        holder.number!!.text = contact.mainPhoneNumber
        holder.dragHandle!!.setOnTouchListener { v: View?, event: MotionEvent ->
            if (event.actionMasked ==
                    MotionEvent.ACTION_DOWN) {
                mItemTouchHelperListener!!.onStartDrag(holder)
            }
            false
        }
        holder.removeItem!!.setOnClickListener { v: View? -> onItemDismiss(holder.adapterPosition) }
        val itemRoot = holder.itemView as ConstraintLayout
        val set = ConstraintSet()
        val layoutId = if (mEditModeEnabled) R.layout.list_item_editable_modified else R.layout.list_item_editable
        set.load(mContext, layoutId)
        set.applyTo(itemRoot)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {

        //Switch in database
        val firstContact = mData!![fromPosition]
        val secondContact = mData!![toPosition]
        val temp = firstContact!!.contactId
        firstContact.contactId = secondContact!!.contactId
        secondContact.contactId = temp
        mRepository.update(firstContact, secondContact)

        //Switch in list - has to be this way for smooth dragging
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mData, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mData, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        //Remove in database
        val id = mData!![position]!!.contactId

        //Remove in list
        mData?.removeAt(position)
        mRepository.deleteContact(id)
        notifyItemRemoved(position)
    }

    /**
     * Sets the data by a given Contact List
     *
     * @param data
     */
    fun setData(data: ArrayList<Contact?>?) {
        if (mData != null) return
        mData = data
        notifyDataSetChanged()
    }

    /**
     * Ummm... Enables edit mode
     *
     * @param enable true/false
     */
    fun enableEditMode(enable: Boolean) {
        if (mEditModeEnabled == enable) return
        mEditModeEnabled = enable

        //Animate all the RecyclerView items:
        for (i in 0 until itemCount) {
            val holder = mRecyclerView.findViewHolderForAdapterPosition(i) as ContactHolder?
            holder?.animate()
        }
    }

    inner class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView), ItemTouchHelperViewHolder {
        @BindView(R.id.item_photo)
        var image: ImageView? = null

        @BindView(R.id.item_big_text)
        var name: TextView? = null

        @BindView(R.id.item_small_text)
        var number: TextView? = null

        @BindView(R.id.drag_handle)
        var dragHandle: ImageView? = null

        @BindView(R.id.item_remove)
        var removeItem: ImageView? = null
        override fun onItemSelected() {
            enableEditMode(true)
            mItemTouchHelperListener?.onItemSelected(this)
        }

        override fun onItemClear() {}

        /**
         * Animates the ContactHolder
         */
        fun animate() {
            val itemRoot = itemView as ConstraintLayout
            val set = ConstraintSet()
            set.clone(itemRoot)
            val transition: Transition = AutoTransition()
            transition.interpolator = OvershootInterpolator()
            TransitionManager.beginDelayedTransition(itemRoot, transition)
            val layoutId = if (mEditModeEnabled) R.layout.list_item_editable_modified else R.layout.list_item_editable
            set.load(mContext, layoutId)
            set.applyTo(itemRoot)
        }

        init {
            ButterKnife.bind(this, itemView)
        }
    }

    init {
        mRepository = DataRepository.getInstance(AppDatabase.getDatabase(mContext))
    }
}