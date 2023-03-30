package com.dev.nycschools.screens.school

import android.Manifest.permission.CALL_PHONE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dev.nycschools.R
import com.dev.nycschools.models.school.School
import java.util.*


/**
 * Adapter for School screen recyclerview.
 */
class SchoolRecyclerViewAdapter(
    val context: Context,
    list: ArrayList<School?>,
    listener: ItemClickListener? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val viewTypeItem = 0
    private val viewTypeLoading = 1
    private var mItemList = list
    private var mListener: ItemClickListener? = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeItem) {
            val view: View =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.landing_list_view_item, parent, false)
            LoginFragViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.landing_list_view_loading, parent, false)
            LoadingViewHolder(view)
        }

    }

    // getItemViewType() method is the method where we check each element
    // of the list. If the element is NULL we set the view type as 1 else 0
    override fun getItemViewType(position: Int): Int {
        return if (mItemList[position] == null) viewTypeLoading else viewTypeItem
    }

    fun updateListItems(updatedList: ArrayList<School?>) {
        mItemList.clear()
        mItemList = updatedList
        notifyDataSetChanged()
    }

    fun updateMoreListItems(updatedList: ArrayList<School>) {
        val positionStart = mItemList.size
        mItemList.addAll(updatedList)
        notifyItemRangeInserted(positionStart, updatedList.size)
    }

    override fun getItemCount(): Int {
        return mItemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoginFragViewHolder) {
            val model: School? = mItemList[position]
            holder.personName.text = model?.schoolName
            holder.address.text =
                "${model?.primaryAddressLine1} ${model?.city}  ${model?.zip}  ${model?.stateCode}"
            holder.stuCount.text = "Total Student : ${model?.totalStudents}"
            holder.weblink.setOnClickListener {
                try {
                    model?.website?.let {
                        var url = it
                        if (!it.startsWith("https://") && !it.startsWith("http://")) {
                            url = "http://$it"
                        }
                        val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(myIntent)
                    }
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context, "No application can handle this request."
                                + " Please install a webbrowser", Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
            holder.call.setOnClickListener {
                try {
                    model?.phoneNumber?.let {
                        val uri = "tel:" + it.trim()
                        val intent = Intent(Intent.ACTION_CALL)
                        intent.data = Uri.parse(uri)
                        if (ContextCompat.checkSelfPermission(
                                context,
                                CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            context.startActivity(intent)
                        } else {
                            requestPermissions(context as Activity, arrayOf(CALL_PHONE), 1)
                        }
                    }
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context, "Sorry! Unable to open", Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
            holder.mail.setOnClickListener {
                try {
                    model?.schoolEmail?.let {
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:") // only email apps should handle this
                        context.startActivity(intent)
                    }
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context, "Sorry! Unable to open", Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
            holder.location.setOnClickListener {
                try {
                    model?.let {
                        val uri = "geo:${model.latitude},${model.longitude}"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        context.startActivity(intent)
                    }
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(
                        context, "Sorry! Unable to open", Toast.LENGTH_LONG
                    ).show()
                    e.printStackTrace()
                }
            }
            holder.item.setOnClickListener {
                if (model != null) {
                    mListener?.onItemClick(holder.item, model)
                }
            }

        }
    }

    fun startLoadMoreProgress() {
        mItemList.add(null)
        notifyItemInserted(mItemList.size - 1)
    }

    fun stopLoadMoreProgress() {
        mItemList.removeAt(mItemList.size - 1)
        val scrollPosition: Int = mItemList.size
        notifyItemRemoved(scrollPosition + 1)
    }

    class LoginFragViewHolder(val item: View) : RecyclerView.ViewHolder(item) {
        val personName: TextView = item.findViewById(R.id.schoolName)
        val address: TextView = item.findViewById(R.id.address)
        val stuCount: TextView = item.findViewById(R.id.totalStudent)
        val weblink: ImageView = item.findViewById(R.id.website)
        val call: ImageView = item.findViewById(R.id.call)
        val mail: ImageView = item.findViewById(R.id.mail)
        val location: ImageView = item.findViewById(R.id.location)

    }

    class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
}