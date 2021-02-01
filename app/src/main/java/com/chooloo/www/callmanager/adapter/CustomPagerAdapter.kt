package com.chooloo.www.callmanager.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.chooloo.www.callmanager.R
import com.chooloo.www.callmanager.ui.fragment.CGroupsFragment
import com.chooloo.www.callmanager.ui.fragment.ContactsPageFragment
import com.chooloo.www.callmanager.ui.fragment.RecentsPageFragment
import com.chooloo.www.callmanager.util.PreferenceUtils
import java.lang.reflect.InvocationTargetException
import java.util.*

class CustomPagerAdapter(// -- Constants -- //
        private val mContext: Context, fragmentManager: FragmentManager?) : FragmentPagerAdapter(fragmentManager) {
    private val mClasses: MutableList<Class<*>> = ArrayList(Arrays.asList(RecentsPageFragment::class.java, ContactsPageFragment::class.java, CGroupsFragment::class.java))
    private val mTitles: MutableList<String> = ArrayList(Arrays.asList("Recents", "Contacts", "Excel"))

    /**
     * Toggle excel tab by a given boolean
     *
     * @param isShow show or don't show the excel tab
     */
    private fun toggleExcelTab(isShow: Boolean) {
        if (!isShow && mClasses.contains(CGroupsFragment::class.java)) {
            mClasses.remove(CGroupsFragment::class.java)
            mTitles.remove("Excel")
        } else if (isShow && !mClasses.contains(CGroupsFragment::class.java)) {
            mClasses.add(CGroupsFragment::class.java)
            mTitles.add("Excel")
        }
    }

    /**
     * Returns the amount of pages
     *
     * @return
     */
    override fun getCount(): Int {
        return mClasses.size
    }

    /**
     * Returns an item by its position
     *
     * @param position the position of the page
     * @return Fragment the fragment representing the page itself
     */
    override fun getItem(position: Int): Fragment? {
        try {
            return mClasses[position].getDeclaredConstructor(*arrayOf<Class<*>>(Context::class.java)).newInstance(mContext) as Fragment
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * Returns the pages title by his position
     *
     * @param position position of the page
     * @return String the string representing the title of the page
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return try {
            mTitles[position]
        } catch (e: IndexOutOfBoundsException) {
            null
        }
    }

    /**
     * Constructor
     *
     * @param context
     * @param fragmentManager
     */
    init {

        // Enable excel tab by the preference
        PreferenceUtils.getInstance(mContext)
        val isEnableExcel = PreferenceUtils.getInstance().getBoolean(R.string.pref_excel_enable_key)
        toggleExcelTab(isEnableExcel)
    }
}