package com.chooloo.www.callmanager.task

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import com.chooloo.www.callmanager.database.AppDatabase.Companion.getDatabase
import com.chooloo.www.callmanager.database.DataRepository.Companion.getInstance
import com.chooloo.www.callmanager.database.entity.CGroup
import com.chooloo.www.callmanager.database.entity.Contact
import com.chooloo.www.callmanager.util.Utilities
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class AsyncSpreadsheetImport
/**
 * Create an instance of [AsyncSpreadsheetImport].
 *
 * @param context        Needed to access the database.
 * @param excelFile      The file to import contacts from.
 * @param nameColIndex   The contact's name column index.
 * @param numberColIndex The contact's number column index.
 * @param CGroup         A list to import the data into. Can be empty (not in the database).
 */(@field:SuppressLint("StaticFieldLeak") private val mContext: Context,
    private val mExcelFile: File,
    private val mNameColIndex: Int,
    private val mNumberColIndex: Int,
    private val mCGroup: CGroup) : AsyncTask<Void, Int, List<Contact?>?>() {
    private var status = STATUS_SUCCESSFUL
    private var mOnProgressListener: OnProgressListener? = null
    private var mOnFinishListener: OnFinishListener? = null
    private var mRowCount = 0

    /**
     * Sets onProgressListener
     *
     * @param listener
     */
    fun setOnProgressListener(listener: OnProgressListener?) {
        mOnProgressListener = listener
    }

    /**
     * Sets onFinishListener
     *
     * @param listener
     */
    fun setOnFinishListener(listener: OnFinishListener?) {
        mOnFinishListener = listener
    }

    protected override fun doInBackground(vararg voids: Void): List<Contact?>? {
        var listId = mCGroup.listId
        val repository = getInstance(getDatabase(mContext)!!)
        if (listId == 0L) { //If this list isn't in the database
            listId = repository!!.insertCGroups(mCGroup)!![0]
        }
        val contacts = fetchContacts(listId)
        if (contacts == null) {
            repository!!.deleteCGroup(listId)
            return null
        }
        repository!!.insertContacts(contacts)
        return contacts
    }

     override fun onProgressUpdate(vararg values: Int?) {
        val rowsRead = values[0]
        if (mOnProgressListener != null) mOnProgressListener!!.onProgress(rowsRead!!, mRowCount)
    }

    override fun onPostExecute(contacts: List<Contact?>?) {
        if (status == STATUS_FAILED) {
            Toast.makeText(mContext, "Couldn't find contacts in the spreadsheet specified", Toast.LENGTH_LONG).show()
            Timber.w("Couldn't find contacts in %s at the columns %d and %d", mExcelFile.path, mNameColIndex, mNumberColIndex)
        } else if (status == STATUS_FILE_NOT_FOUND) {
            Toast.makeText(mContext, "Couldn't find the file specified", Toast.LENGTH_SHORT).show()
        }
        if (mOnFinishListener != null) mOnFinishListener!!.onFinish(status)
    }

    /**
     * Fetches contacts by list id
     *
     * @param listId
     * @return List<Contact>
    </Contact> */
    private fun fetchContacts(listId: Long): List<Contact?>? {
        val contacts: MutableList<Contact?> = ArrayList()
        try {
            val workbook: Workbook = HSSFWorkbook(FileInputStream(mExcelFile))
            val sheet = workbook.getSheetAt(0)
            val rowIterator = sheet.rowIterator()
            mRowCount = sheet.physicalNumberOfRows
            var rowsRead = 0
            while (rowIterator.hasNext()) {
                val row = rowIterator.next()
                if (row.getCell(mNameColIndex) == null || row.getCell(mNumberColIndex) == null) continue
                var name = row.getCell(mNameColIndex).stringCellValue
                val cellType = row.getCell(mNumberColIndex).cellType
                var phoneNumber: String? = null
                when (cellType) {
                    CellType.NUMERIC -> {
                        val number = row.getCell(mNumberColIndex).numericCellValue
                        phoneNumber = String.format(Utilities.sLocale, "%.0f\n", number)
                    }
                    CellType.STRING -> phoneNumber = row.getCell(mNumberColIndex).stringCellValue
                }
                if (phoneNumber == null) continue
                if (name == null) name = phoneNumber
                val contact = Contact(name, phoneNumber, null)
                contact.listId = listId
                contacts.add(contact)
                rowsRead++
                publishProgress(rowsRead)
            }
        } catch (e: FileNotFoundException) {
            Timber.e(e)
            status = STATUS_FILE_NOT_FOUND
            return null
        } catch (e: IOException) {
            Timber.e(e)
            status = STATUS_FAILED
            return null
        }
        return contacts
    }

    interface OnProgressListener {
        fun onProgress(rowsRead: Int?, rowsCount: Int)
    }

    interface OnFinishListener {
        fun onFinish(callback: Int)
    }

    companion object {
        // Constants
        const val STATUS_SUCCESSFUL = 0
        const val STATUS_FAILED = 1
        const val STATUS_FILE_NOT_FOUND = 2
    }
}