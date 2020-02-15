package com.sr.pregnancycalc

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.sr.pregnancycalc.Data.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@Suppress("DEPRECATION")

var alertDate = Eng.eng.alertStr
var months = monthsEn
var vveeksString = Eng.eng.weekSt
var dayString = Eng.eng.daySt
var langChosen = "English"


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Lang preferences

        val russian = getSharedPreferences("russian", Context.MODE_PRIVATE)
        val russianOpt = russian.getBoolean("russianOpt", false)
        val rusiianEditor = russian.edit()

        //   first time disclaimer //

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)
        val editor = prefs.edit()
        if (firstStart) {
            val builder = android.app.AlertDialog.Builder(this)
                .setMessage(notification)
                .setPositiveButton(
                    "Agree and Proceed",
                    { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                        editor.putBoolean("firstStart", false)
                        editor.apply()
                    })
                .setNegativeButton("EXIT", { dialogInterface: DialogInterface, i: Int ->
                    finish()
                })
                .show()
        }

        // Language changing
        fun toRussian() {
            months = monthRu
            alertDate = Rus.rus.alertStr
            vveeksString = Rus.rus.weekSt
            dayString = Rus.rus.daySt
            pickDateBtn.text = Rus.rus.one
            pickDateBtn.textSize = 14F
            textAgeLeft.text = Rus.rus.two
            textAgeLeft.textSize = 16F
            textEDDLeft.text = Rus.rus.tree
            textEDDLeft.textSize = 14F
            textTermLeft.text = Rus.rus.four
            textTermLeft.textSize = 14F
        }

        fun toEnglish() {
            alertDate = Eng.eng.alertStr
            months = monthsEn
            vveeksString = Eng.eng.weekSt
            dayString = Eng.eng.daySt
            pickDateBtn.text = Eng.eng.one
            pickDateBtn.textSize = 18F
            textAgeLeft.text = Eng.eng.two
            textAgeLeft.textSize = 18F
            textEDDLeft.text = Eng.eng.tree
            textEDDLeft.textSize = 18F
            textTermLeft.text = Eng.eng.four
            textTermLeft.textSize = 18F
        }
        if (russianOpt == true) {
            toRussian()
        } else {
            toEnglish()
        }

        lngBtn.setOnClickListener(){
            ruBtn.isVisible = true
            enBtn.isVisible = true
            lngBtn.isVisible = false
            lmpText.text = ""
            textAgeRight.text = ""
            textEDDRight.text = ""
            textTermRight.text = ""
        }

        ruBtn.setOnClickListener() {
            rusiianEditor.putBoolean("russianOpt", true)
            rusiianEditor.apply()
            toRussian()
            ruBtn.isVisible = false
            enBtn.isVisible = false
            lngBtn.isVisible = true
                    }
        enBtn.setOnClickListener() {
            rusiianEditor.putBoolean("russianOpt", false)
            rusiianEditor.apply()
            toEnglish()
            ruBtn.isVisible = false
            enBtn.isVisible = false
            lngBtn.isVisible = true
        }

        // create calender dialog and calculate the dates  //

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        var final = 0
        var add = 0
        val reminder = getSharedPreferences("reminder", Context.MODE_PRIVATE)
        var reminderPressed = getSharedPreferences("reminderPresseed", Context.MODE_PRIVATE)
        var ratePresssed = reminderPressed.getBoolean("ratePressed", true)
        var numberOfStarts = reminder.getInt("number", add)
        val editorTwo = reminderPressed.edit()
        val editorOne = reminder.edit()

        fun rateReminder(){
            var some = reminder.getInt("number", add)
            var add = some + 1
            editorOne.putInt("number", add)
            editorOne.apply()
            if (ratePresssed == true && reminder.getInt("number", add) % 10 == 0) {
                val reminderDialog: AlertDialog.Builder = android.app.AlertDialog.Builder(this)
                kotlin.with(reminderDialog) {
                    setMessage(rateMessage)
                    setPositiveButton(
                        "RATE",
                        { dialogInterface: DialogInterface, i: Int ->
                            dialogInterface.dismiss()
                            editorTwo.putBoolean("ratePressed", false)
                            editorTwo.apply()
                            val link: String =
                                "https://play.google.com/store/apps/details?id=com.sr.pregnancycalc"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            startActivity(intent)
                        })
                    setNeutralButton("REMIND LATER") { dialogInterface: DialogInterface?, which: Int ->
                        if (dialogInterface != null) {
                            dialogInterface.dismiss()
                        }
                    }
                }
                    .show()
            }
        }

        pickDateBtn.setOnClickListener {
            rateReminder()
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val month = month + 1
                    val choice = Date(year - 1900, month - 1, dayOfMonth)
                    val today = Date()
                    val difference = today.getTime() - choice.getTime()
                    val gAgeDaysTotsl = difference / (1000 * 3600 * 24)
                    val gAgeWeeks = gAgeDaysTotsl / 7.toDouble()
                    val gAgeWeeksRound = gAgeWeeks.toInt()
                    val gAgeDays = (gAgeDaysTotsl - (gAgeWeeksRound * 7)).toInt()
                    val edd = Date(year - 1900, month - 1, dayOfMonth + 280)
                    val term = Date(year - 1900, month - 1, dayOfMonth + 259)
                    val eddMInd = edd.month
                    val eddMName = months[eddMInd]
                    val eddYName = edd.year % 100
                    val termMInd = term.month
                    val termMName = months[termMInd]
                    val termYName = term.year % 100
                    val lmpMonthName = months[month - 1]
                    lmpText.text = ("" + dayOfMonth + ". " + "$lmpMonthName" + " . " + year)
                    textAgeRight.text =
                        ("$gAgeWeeksRound" + "  $vveeksString" + "  $gAgeDays" + "  $dayString" + "  = " + " $gAgeDaysTotsl" + " $dayString")
                    textEDDRight.text =
                        ("${edd.date}" + " . " + "$eddMName" + " . " + "20$eddYName")
                    textTermRight.text =
                        ("${term.date}" + " . " + "$termMName" + " . " + "20$termYName")
                    if (gAgeDaysTotsl <= 0 || gAgeDaysTotsl > 295) {
                        lmpText.text = (alertDate)
                        textAgeRight.text = (alertDate)
                        textEDDRight.text = (alertDate)
                        textTermRight.text = (alertDate)
                    }
                },
                year,
                month,
                day
            )
            dpd.show()
        }





        // Video banner

        val videoPath: String = "android.resource://" + packageName + "/" + R.raw.tryvideo
        val uri = Uri.parse(videoPath)
        videoView.setVideoURI(uri)
        videoView.start()
        videoView.setOnPreparedListener(object : MediaPlayer.OnPreparedListener {
            override fun onPrepared(mediaPlayer: MediaPlayer) {
                mediaPlayer.isLooping = true
            }
        })


        // Open link to another app
        val link: String = "https://play.google.com/store/apps/details?id=com.sr.scrolldata"
        linkBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
        }
    }

    // Restart video after onPause
    override fun onResume() {
        super.onResume()
        videoView.start()
    }

}

