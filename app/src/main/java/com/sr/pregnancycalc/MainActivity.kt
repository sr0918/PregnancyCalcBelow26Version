package com.sr.pregnancycalc

import android.annotation.SuppressLint
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
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@Suppress("DEPRECATION")

var alertDate = "Please, choose valid LMP!"
var months = arrayListOf(
"January",
"February",
"March",
"April",
"May",
"June",
"July",
"August",
"September",
"October",
"November",
"December"
)
var vveeksString = "weeks"
var dayString = "days"
var langChosen = "English"


class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Add langImage to Context Menu
        registerForContextMenu(langImage)

        // Lang preferences

        val russian = getSharedPreferences("russian", Context.MODE_PRIVATE)
        val russianOpt = russian.getBoolean("russianOpt", false)
        val rusiianEditor = russian.edit()
        if (langChosen == "Russian"){
            rusiianEditor.putBoolean("russianOpt", true)
            pickDateBtn.text = "ПЕРВЫЙ ДЕНЬ ПОСЛЕДНИХ МЕСЯЧНЫХ"
            textAgeLeft.text = "СРОК БЕРЕМЕННОСТИ"
            textEDDLeft.text = "ПРЕДПОЛАГАЕМАЯ ДАТА РОДОВ (40н)"
            textTermLeft.text = "ДОНОШЕННАЯ БЕРЕМЕННОСТЬ (37н)"
            alertDate = "Выберите правильную дату!"
            months = arrayListOf(
                "Января",
                "Февраля",
                "Марта",
                "Апреля",
                "Мая",
                "Июня",
                "Июля",
                "Августа",
                "Сентября",
                "Октября",
                "Ноября",
                "Декабря"
            )
            dayString = "дн"
            vveeksString = "нед"

        }
        //   first time disclaimer //

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstStart = prefs.getBoolean("firstStart", true)
        val editor = prefs.edit()
        if (firstStart) {
            val builder = android.app.AlertDialog.Builder(this)
                .setMessage(
                    "All information contained in and produced by the Pregnancy Age Calculator is provided for educational purposes only. This information should not be used for the diagnosis or treatment of any health problem or disease. This information is not intended to replace clinical judgment or guide individual patient care in any manner.\n" +
                            "The User is hereby notified that the information contained herein may not meet the user’s needs.\n" +
                            "The User of this software assumes sole responsibility for any decisions made or actions taken based on the information contained in the application.\n" +
                            "Neither the author nor any other party involved in the preparation, publication or distribution of the Pregnancy Age Calculator shall be liable for any special, consequential, or exemplary damages resulting in whole or part from any User’s use of or reliance upon this system and the information contained within.\n" +
                            "The publisher and developer disclaim all warranties regarding such information whether express or implied, including any warranty as to the quality, accuracy, currency or suitability of this information for any particular purpose.\n" +
                            "By using the Pregnancy Age Calculator, documentation and/or any software found therein, the User agrees to abide by United States and International copyright laws and all other applicable laws involving copyright.\n"
                )
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


        // create calender dialog and calculate the dates  //

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        pickDateBtn.setOnClickListener {
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
    //     Menu Step 2 get method by CTRL+O
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.lang, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }


    //     Menu Step 3 get method by CTRL+O
    @SuppressLint("SetTextI18n")
    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.russian)
            langChosen = "Russian"
            pickDateBtn.text = "ПЕРВЫЙ ДЕНЬ ПОСЛЕДНИХ МЕСЯЧНЫХ"
            textAgeLeft.text = "СРОК БЕРЕМЕННОСТИ"
            textEDDLeft.text = "ПРЕДПОЛАГАЕМАЯ ДАТА РОДОВ (40н)"
            textTermLeft.text = "ДОНОШЕННАЯ БЕРЕМЕННОСТЬ (37н)"
            alertDate = "Выберите правильную дату!"
            months = arrayListOf(
            "Января",
            "Февраля",
            "Марта",
            "Апреля",
            "Мая",
            "Июня",
            "Июля",
            "Августа",
            "Сентября",
            "Октября",
            "Ноября",
            "Декабря"
        )
            dayString = "дн"
            vveeksString = "нед"

        /*when (item.itemId) {
            R.id.english -> println("1")
             -> pickDateBtn.text = "ПЕРВЫЙ ДЕНЬ ПОСЛЕДНИХ МЕСЯЧНЫХ"


        }*/
        return super.onContextItemSelected(item)
    }
}
