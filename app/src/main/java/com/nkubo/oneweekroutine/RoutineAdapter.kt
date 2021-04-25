package com.nkubo.oneweekroutine

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getColor
import kotlin.collections.ArrayList


class RoutineAdapter(context: Context, private val list: ArrayList<RoutineList>, val height:Float,val density:Float) : BaseAdapter(){
    private var inflater : LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
    val handler = Handler()
    val bg= getColor(context,R.color.bg)
    val white= getColor(context,R.color.white)
    val clear= getColor(context,R.color.clear)
    val animeDeleteCell = AnimationUtils.loadAnimation(context, R.anim.deletecell)
    lateinit var listener: OnItemClickListener


    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder : ViewHolder? = null
        var view: View? =convertView
        
        if (view == null) {

            view = inflater!!.inflate(R.layout.routin_cell, parent, false)
            view.layoutParams.height= (density*height/11.5).toInt()

            viewHolder = ViewHolder(
                    view.findViewById(R.id.lineLayout),
                    view.findViewById(R.id.button_Name),
                    view.findViewById(R.id.button_Delete),
                    view.findViewById(R.id.textView_Check),
                    view.findViewById(R.id.textView_Name),
                    view.findViewById(R.id.cardView)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

       viewHolder.textView_Name.text=list[position].name

        viewHolder.textView_Check.textSize= (height/20.9).toFloat()
        viewHolder.textView_Name.textSize= (height/32.6).toFloat()
        viewHolder.button_Delete.textSize= (height/13.6).toFloat()


        if (list[position].check==false){
            viewHolder.textView_Check.setAlpha(0.0F)
            viewHolder.lineLayout.alpha=0.0F

        }else{
            viewHolder.textView_Check.setAlpha(1.0F)
            viewHolder.lineLayout.alpha=1.0F
        }

        viewHolder.button_Name.setOnClickListener {
            listener.cellupdate(it,position)
            this.notifyDataSetChanged()

        }
        viewHolder.button_Delete.setOnClickListener {
            listener.stopWindow(it,position)
            viewHolder.button_Delete.isEnabled=false
            if (view != null) {
                view.startAnimation(animeDeleteCell)
            }
            Handler().postDelayed({
                listener.celldelete(it,position)
                this.notifyDataSetChanged()
            }, 330)
            viewHolder.button_Delete.isEnabled=true
        }

        if(list[position].name==""){
            viewHolder.cardView.alpha=0.0F
        }else{
            viewHolder.cardView.alpha=1.0F
        }

        return view
    }

    interface OnItemClickListener{
        fun cellupdate(view: View, position: Int)
        fun celldelete(view: View, position: Int)
        fun stopWindow(view: View, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}
data class ViewHolder(var lineLayout: LinearLayout,var button_Name: Button, val button_Delete: Button, val textView_Check: TextView, val textView_Name: TextView,val cardView:CardView) {}