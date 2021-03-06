package com.nkubo.oneweekroutine


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity() : AppCompatActivity(){
    var save=Save()
    var routineList = ArrayList<RoutineList>()
    var recodeList = ArrayList<RecodeList>()
    val handler = Handler()
    var timeValue = 0
    val EEEEFormat = SimpleDateFormat("EEEE", Locale.JAPAN)
    val yyyyFormat=SimpleDateFormat("yyyy")
    val MFormat=SimpleDateFormat("M")
    val dFormat=SimpleDateFormat("d")
    val HHFormat=SimpleDateFormat("HH")
    val mmFormat=SimpleDateFormat("mm")
    var jsonSave :String=""
    var jsonRoutineList :String=""
    var jsonRecodeList :String=""
    var bg:Int=0
    var main:Int=0
    var height:Float = 0F
    var width:Float = 0F
    var density:Float = 0F
    val gson = Gson()

    lateinit var sharedPref: SharedPreferences
    lateinit var animeAddCell:Animation
    lateinit var listView:ListView
    lateinit var button_AddRoutine:Button
    lateinit var button_Day:Button
    lateinit var button_Recode:Button
    lateinit var textView_Percent:TextView
    lateinit var textView_RemainingTime:TextView
    lateinit var pieChart:PieChart
    lateinit var dm:DisplayMetrics

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        animeAddCell = AnimationUtils.loadAnimation(this.applicationContext, R.anim.addcell)
        bg= getColor(R.color.bg)
        main= getColor(R.color.main)
        listView = findViewById<ListView>(R.id.listView)
        button_AddRoutine = findViewById<Button>(R.id.button_AddRoutine)
        button_Day = findViewById<Button>(R.id.button_Day)
        button_Recode = findViewById<Button>(R.id.button_Recode)
        textView_Percent = findViewById<TextView>(R.id.textView_Percent)
        textView_RemainingTime = findViewById<TextView>(R.id.textView_RemainingTime)
        pieChart = findViewById<PieChart>(R.id.pieChart)
        dm = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(dm)
        height = dm.heightPixels/dm.density
        width = dm.widthPixels/dm.density
        density=dm.density

        val readSave: String? = sharedPref.getString("save", "")
        val readRecodeList: String? = sharedPref.getString("recodeList", "")
        val readRoutineList: String? = sharedPref.getString("routineList", "")
        val RecodeType = object : TypeToken<List<RecodeList>>() {}.type
        val RoutineType = object : TypeToken<List<RoutineList>>() {}.type

        button_AddRoutine.textSize= (height/48)
        button_Day.textSize= (height/48)
        button_Recode.textSize= (height/48)
        textView_RemainingTime.textSize= (height/32.6).toFloat()

        if(height<590){
            textView_Percent.textSize=(height/19)
        }else{
            textView_Percent.textSize=(height/13.3).toFloat()
        }

        if (readSave != "") {
            save = Gson().fromJson(readSave, Save::class.java)
        }
        if (readRecodeList != "") {
            recodeList = Gson().fromJson<List<RecodeList>>(readRecodeList, RecodeType) as ArrayList<RecodeList>
        }
        if (readRoutineList != "") {
            routineList = Gson().fromJson<List<RoutineList>>(readRoutineList, RoutineType) as ArrayList<RoutineList>
        }

        val arrayAdapter = RoutineAdapter(this, routineList,height,density)
        listView.setBackgroundColor(bg)
        listView.adapter = arrayAdapter
        pieChartProsecc()
        textView_Percent.text = save.nowPercent + "%"


        when (save.deadlineDayWeek) {
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Sunday)
            }
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Monday)
            }
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Tuesday)
            }
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Wednesday)
            }
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Thursday)
            }
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Friday)
            }
            "?????????" -> {
                button_Day.setText(R.string.button_Day_Saturday)
            }
        }

        if(save.deadlineDay==""){
            updateDeadline()
            jsonSave=gson.toJson(save)
            sharedPref.edit().putString("save",jsonSave).apply()
        }

        val runnable = object : Runnable {
            override fun run() {
                timeValue++
                handler.postDelayed(this, 1000)
                timeProcess()
                val nowYear:String =yyyyFormat.format(Date())
                val nowMonth:String =MFormat.format(Date())
                val nowDay:String =dFormat.format(Date())

             if(save.deadlineYear.toInt()<nowYear.toInt()){
                    addRecode()
                    updateDeadline()
                    arrayAdapter.notifyDataSetChanged()
                    saveProsecc()
                    percentProcess(routineList)
                    pieChartProsecc()

                }else if(save.deadlineYear.toInt()==nowYear.toInt() && save.deadlineMonth.toInt()<nowMonth.toInt()){
                    addRecode()
                    updateDeadline()
                    arrayAdapter.notifyDataSetChanged()
                    saveProsecc()
                    percentProcess(routineList)
                    pieChartProsecc()
                }else if(save.deadlineYear.toInt()==nowYear.toInt() && save.deadlineMonth.toInt()==nowMonth.toInt() && save.deadlineDay.toInt()<nowDay.toInt()){
                    addRecode()
                    updateDeadline()
                    arrayAdapter.notifyDataSetChanged()
                    saveProsecc()
                    percentProcess(routineList)
                    pieChartProsecc()
                }

            }
        }
        handler.post(runnable)

        arrayAdapter.setOnItemClickListener(object:RoutineAdapter.OnItemClickListener{
            override fun cellupdate(view: View, position: Int) {
                if(routineList[position].name==""){
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    for(i in 0..routineList.size-1){
                        if(routineList[i].name==""){
                            routineList.removeAt(i)
                        }
                    }
                    percentProcess(routineList)
                    Handler().postDelayed({
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }, 500)
                    saveProsecc()
                }else {
                    if(routineList[position].check==true){
                        routineList[position].check=false
                    }else{
                        routineList[position].check=true
                    }
                    arrayAdapter.notifyDataSetChanged()
                    if (routineList.size != 0) {
                        for(i in 0..routineList.size-1){
                            if(routineList[i].name==""){
                                routineList.removeAt(i)
                            }
                        }
                    }
                    percentProcess(routineList)
                    pieChartProsecc()
                }
                saveProsecc()
            }

            override fun celldelete(view: View, position: Int) {
                if(routineList[position].name==""){
                    for(i in 0..routineList.size-1){
                        if(routineList[i].name==""){
                            routineList.removeAt(i)
                        }
                    }
                    percentProcess(routineList)
                }else {
                    routineList.removeAt(position)
                    if (routineList.size != 0) {
                        for(i in 0..routineList.size-1){
                            if(routineList[i].name==""){
                                routineList.removeAt(i)
                            }
                        }
                    }
                }
                Handler().postDelayed({
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    percentProcess(routineList)
                    pieChartProsecc()
                    saveProsecc()
                }, 500)
                saveProsecc()
            }

            override fun stopWindow(view: View, position: Int) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

        button_AddRoutine.setOnClickListener {
            if (routineList.size != 0) {
                for(i in 0..routineList.size-1){
                    if(routineList[i].name==""){
                        routineList.removeAt(i)
                        arrayAdapter.notifyDataSetChanged()
                    }
                }
            }
            val dummy = RoutineList()
            routineList.add(dummy)
            arrayAdapter.notifyDataSetChanged()

            val title=TextView(this)

            title.text="????????????????????????"
            title.setBackgroundColor(main)
            title.setTextColor(getColor(R.color.white))
            title.setTextSize(24F)
            title.setGravity(Gravity.CENTER)
            title.setPadding(10,10,10,10)

            val myedit = EditText(this)
            myedit.inputType = InputType.TYPE_CLASS_TEXT

            val builder=AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                    builder.setCustomTitle(title)
                    .setMessage("??????????????????????????????????????????")
                    .setView(myedit)
                    .setPositiveButton("??????", { dialog, which ->
                        if (myedit.text.toString() != "") {
                            routineList[routineList.size-1].name=myedit.text.toString()
                            if(listView.getChildAt(routineList.size-1)!=null){
                                listView.getChildAt(routineList.size-1).startAnimation(animeAddCell)
                            }
                            arrayAdapter.notifyDataSetChanged()
                            if (routineList.size != 0) {
                                for(i in 0..routineList.size-1){
                                    if(routineList[i].name==""){
                                        routineList.removeAt(i)
                                    }
                                }
                            }
                            percentProcess(routineList)
                            saveProsecc()
                            pieChartProsecc()
                        }
                    })
                    .setNegativeButton("???????????????", { dialog, which ->
                        routineList.removeAt(routineList.size-1)
                        arrayAdapter.notifyDataSetChanged()
                    })
                    .show()
        }

        button_Day.setOnClickListener {
            val strList = arrayOf("?????????", "?????????", "?????????", "?????????", "?????????", "?????????", "?????????")
            val title=TextView(this)
            title.text="?????????(??????)???????????????????????????"
            title.setBackgroundColor(main)
            title.setTextColor(getColor(R.color.white))
            title.setTextSize(22F)
            title.setGravity(Gravity.CENTER)
            title.setPadding(10,10,10,10)

            val builder=AlertDialog.Builder(this,R.style.MyAlertDialogStyle)
                    builder.setCustomTitle(title)
                    .setItems(strList) { dialog, which ->
                        when (which) {
                            0 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Sunday)
                            }
                            1 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Monday)
                            }
                            2 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Tuesday)
                            }
                            3 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Wednesday)
                            }
                            4 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Thursday)
                            }
                            5 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Friday)
                            }
                            6 -> {
                                save.deadlineDayWeek = "?????????"
                                button_Day.setText(R.string.button_Day_Saturday)
                            }
                        }
                        updateDeadline()
                        saveProsecc()
                    }
                    .show()
        }
        button_Recode.setOnClickListener{
            jsonRecodeList=gson.toJson(recodeList)
            val intent = Intent(this@MainActivity,RecodeActivity::class.java)
            intent.putExtra("RecodeList",jsonRecodeList)
            intent.putExtra("height",height)
            intent.putExtra("width",width)
            intent.putExtra("density",density)
            startActivity(intent)
        }
    }


    fun percentProcess(routineList: ArrayList<RoutineList>){
        var per: String = "0"
        if (routineList.size != 0) {
            per = (100 * routineList.filter { it.check == true }.size / routineList.size).toString()
            for(i in 0..routineList.size-1){
                if(routineList[i].name==""){
                    routineList.removeAt(i)
                }
            }
        }
        save.nowPercent=per
        textView_Percent.text=save.nowPercent+"%"
    }

    fun timeProcess(){
        var res:String=""
        for(i in 0..6){
            val calendar = Calendar.getInstance()
            calendar.time= Date()
            calendar.add(Calendar.DAY_OF_MONTH,+i)
            val iDayWeek:String=EEEEFormat.format(calendar.time)
            if(iDayWeek==save.deadlineDayWeek){
                if(i+1==1){
                    val nowHH:String=HHFormat.format(calendar.time)
                    val nowmm:String=mmFormat.format(calendar.time)
                    var hh:Int=23-nowHH.toInt()
                    var mm:Int=60-nowmm.toInt()
                    if(mm==60){
                        mm=0
                        hh=hh+1
                    }
                    res="?????????"+hh.toString()+"??????"+mm.toString()+"???"
                }else {
                    res = "?????????" + (i + 1).toString() + "???"
                }
            }
        }
        textView_RemainingTime.text=res
    }

    fun updateDeadline(){
        for(i in 0..6){
            val calendar = Calendar.getInstance()
            calendar.time= Date()
            calendar.add(Calendar.DAY_OF_MONTH,+i)
            val iDayWeek:String=EEEEFormat.format(calendar.time)
            if(iDayWeek==save.deadlineDayWeek){
                save.deadlineYear=yyyyFormat.format(calendar.time)
                save.deadlineMonth=MFormat.format(calendar.time)
                save.deadlineDay=dFormat.format(calendar.time)
            }
        }
    }

    fun addRecode(){
        var tuika=RecodeList()
        tuika.day=save.deadlineMonth+"???"+save.deadlineDay+"???"
        tuika.percent=save.nowPercent
        recodeList.add(0,tuika)
        if(routineList.size!=0){
            for(i in 0..routineList.size-1){
                routineList[i].check=false
            }
        }
    }

    fun pieChartProsecc(){
        var noNowPercent: Float = (100 - save.nowPercent.toInt()).toFloat()
        var entryList = mutableListOf<PieEntry>()
        entryList.add(PieEntry(save.nowPercent.toFloat(),""))
        entryList.add(PieEntry(noNowPercent,""))
        val pieDataSet = PieDataSet(entryList, "")
        pieDataSet.setDrawValues(false)
        pieDataSet.setColors(main,bg)
        pieChart.data = PieData(pieDataSet)
        pieChart.legend.isEnabled = false
        pieChart.isHighlightPerTapEnabled=false
        pieChart.isRotationEnabled=false
        pieChart.holeRadius= 58F
        pieChart.transparentCircleRadius=0F
        pieChart.description=null
        pieChart.invalidate()
        pieChart.animateXY(800,800)
    }
    fun saveProsecc(){
        jsonSave=gson.toJson(save)
        sharedPref.edit().putString("save",jsonSave).apply()
        jsonRoutineList=gson.toJson(routineList)
        sharedPref.edit().putString("routineList",jsonRoutineList).apply()
        jsonRecodeList=gson.toJson(recodeList)
        sharedPref.edit().putString("recodeList",jsonRecodeList).apply()
    }

}


