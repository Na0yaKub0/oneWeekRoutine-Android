package com.nkubo.oneweekroutine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RecodeAdapter(context: Context, private val list: ArrayList<RecodeList>, val totalwidth: Float, val totalheight: Float, val density: Float) : BaseAdapter() {
    private var inflater: LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater!!.inflate(R.layout.recode_cell, parent, false)
        val textView_Achievement = view.findViewById<TextView>(R.id.textView_Achievement)
        val textView_days = view.findViewById<TextView>(R.id.textView_days)
        val textView_Recode_Per = view.findViewById<TextView>(R.id.textView_Recode_Per)

        textView_days.textSize = (totalheight / 81.6).toFloat()
        textView_Achievement.textSize = (totalheight / 27.2).toFloat()
        textView_Recode_Per.textSize = ((totalheight / 54.4).toFloat())

        textView_days.text = " " + list[position].day
        textView_Achievement.text = list[position].percent
        textView_Recode_Per.text = "% "
        view.layoutParams.height = ((totalwidth - (4 * 5 + 4 * 2)) / 6 * density).toInt()
        if (textView_days.text == " ") {
            view.alpha = 0.0F
        } else {
            view.alpha = 1.0F
        }
        return view
    }
}
