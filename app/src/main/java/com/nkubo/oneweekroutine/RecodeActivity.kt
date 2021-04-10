package com.nkubo.oneweekroutine

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecodeActivity : AppCompatActivity() {
    var recodeList = ArrayList<RecodeList>()
    val RecodeType = object : TypeToken<List<RecodeList>>() {}.type
    var pageCount:Int=0
    var barChartPage:Int=1
    var height:Float= 0F
    var width:Float=0F
    var density:Float=0F
    val bg= Color.parseColor("#ffbe91")
    val main= Color.parseColor("#ff8c00")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recode)

        val bundle: Bundle = intent.extras!!
        val readRecodeList: String? = bundle.getString("RecodeList")
        height=bundle.getFloat("height")
        width= bundle.getFloat("width")
        density=bundle.getFloat("density")
        val button_return = findViewById<Button>(R.id.button_return)
        val button_Page_Pluss = findViewById<Button>(R.id.button_Page_Pluss)
        val button_Page_Minus = findViewById<Button>(R.id.button_Page_Minus)
        val textView_AchievementCount = findViewById<TextView>(R.id.textView_AchievementCount)
        val gridView = findViewById<GridView>(R.id.gridView)
        var barChart = findViewById<BarChart>(R.id.barChart)
        pageCount=0
        barChartPage=1
        gridView.setBackgroundColor(bg)

        textView_AchievementCount.textSize= (height/27.2).toFloat()
        button_Page_Minus.textSize= (height/32.6).toFloat()
        button_Page_Pluss.textSize= (height/32.6).toFloat()
        button_return.textSize= (height/48).toFloat()
        button_Page_Minus.text=" <<"
        button_Page_Pluss.text=">> "

        if(readRecodeList!=""){
            recodeList = Gson().fromJson<List<RecodeList>>(readRecodeList, RecodeType) as ArrayList<RecodeList>
        }
        

        textView_AchievementCount.text="達成回数:"+recodeList.filter { it.percent=="100" }.size+"回"



        if(recodeList.size==0){
            val appendValue = RecodeList()
            appendValue.day=""
            appendValue.percent="0"
            pageCount=1
            for (i in 0..5){
                recodeList.add(appendValue)
            }
        }else{
            pageCount=(recodeList.size/6)
            val Remainder=recodeList.size%6
            if (Remainder!=0){
                val appendValue = RecodeList()
                appendValue.day=""
                appendValue.percent="0"
                pageCount=pageCount+1
                for (i in 0..(6-Remainder-1)){
                    recodeList.add(appendValue)
                }
            }
        }


        val arrayAdapter = RecodeAdapter(this, recodeList,width,height,density)
        gridView.adapter = arrayAdapter
        val barDataSet=barChartProsecc()


        barChart.axisRight.axisMinimum= 0F
        barChart.axisLeft.axisMinimum=0F
        barChart.axisRight.axisMaximum=130F
        barChart.axisLeft.axisMaximum=130F
        barChart.xAxis.isGranularityEnabled=false
        barChart.xAxis.position=XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.axisLineColor=bg
        barChart.xAxis.axisLineWidth= 1F
        barChart.xAxis.textColor=main
        barChart.xAxis.labelCount=30
        barChart.xAxis.textSize=(height/74).toFloat()
        barChart.xAxis.granularity= 1F
        barChart.legend.isEnabled=false
        barChart.axisLeft.labelCount=5
        barChart.description.text=""
        barChart.isDoubleTapToZoomEnabled=false
        barChart.isScaleXEnabled=false
        barChart.isScaleYEnabled=false
        barChart.isHighlightPerTapEnabled=false
        barChart.isLongClickable=false
        barChart.isClickable=false
        barChart.setPinchZoom(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisRight.isEnabled=false
        barChart.axisLeft.isEnabled=false

        if(Build.VERSION.SDK_INT>25){
            val kosugimaru_regular = resources.getFont(R.font.kosugimaru_regular)
            barChart.xAxis.typeface=kosugimaru_regular
            barDataSet.valueTypeface=kosugimaru_regular
        }
        val barData = BarData(barDataSet)
        barChart.data=barData
        barChart.xAxis.valueFormatter=  object: ValueFormatter(){
            override fun getFormattedValue(value: Float): String{
                return recodeList[value.toInt()].day
            }
        }

        barChart.animateXY(1000,1000,Easing.EaseInOutQuart)

        button_Page_Pluss.setOnClickListener{
            arrayAdapter.notifyDataSetChanged()
            if(barChartPage!=pageCount){
                barChartPage=barChartPage+1
                val barDataSet=barChartProsecc()
                if(Build.VERSION.SDK_INT>25){
                    val kosugimaru_regular = resources.getFont(R.font.kosugimaru_regular)
                    barDataSet.valueTypeface=kosugimaru_regular
                }
                val barData = BarData(barDataSet)
                barChart.data=barData
                barChart.animateXY(1000,1000,Easing.EaseInOutQuart)
            }
        }

        button_Page_Minus.setOnClickListener {
            arrayAdapter.notifyDataSetChanged()
            if(barChartPage!=1){
                barChartPage=barChartPage-1
                val barDataSet=barChartProsecc()
                if(Build.VERSION.SDK_INT>25){
                    val kosugimaru_regular = resources.getFont(R.font.kosugimaru_regular)
                    barDataSet.valueTypeface=kosugimaru_regular
                }
                val barData = BarData(barDataSet)
                barChart.data=barData
                barChart.animateXY(1000,1000,Easing.EaseInOutQuart)
            }
        }


        button_return.setOnClickListener{
            finish()
        }

    }

    fun barChartProsecc():BarDataSet{
        var entryList = mutableListOf<BarEntry>()

        for(i in (barChartPage-1)*6..(barChartPage*6-1)){
            val value:Float=recodeList[i].percent.toFloat()
            entryList.add(BarEntry(i.toFloat(),value))
        }
        val barDataSet = BarDataSet(entryList,"")
        barDataSet.valueTextColor=main
        barDataSet.color=main
        barDataSet.valueTextSize=(height/74).toFloat()

        barDataSet.valueFormatter=object: ValueFormatter(){
            override fun getBarLabel(barEntry: BarEntry?): String {
                var ans:String=""
                val index=barEntry?.x?.toInt()
                if(recodeList[index!!].day!=""){
                    ans=recodeList[index!!].percent+"%"
                }
                return ans
            }
        }


        return barDataSet
    }


}