package com.sr.pregnancycalc

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //   first time disclaimer //

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        var firstStart = prefs.getBoolean("firstStart", true)
        val editor = prefs.edit()
        if (firstStart) {
            val builder = android.app.AlertDialog.Builder(this)
                .setMessage(
                    "All information contained in and produced by the Pregnancy Dates Calculator is provided for educational purposes only. This information should not be used for the diagnosis or treatment of any health problem or disease. This information is not intended to replace clinical judgment or guide individual patient care in any manner.\n" +
                            "The User is hereby notified that the information contained herein may not meet the user’s needs.\n" +
                            "The User of this software assumes sole responsibility for any decisions made or actions taken based on the information contained in the application.\n" +
                            "Neither the system’s authors nor any other party involved in the preparation, publication or distribution of the Pregnancy Dates Calculator shall be liable for any special, consequential, or exemplary damages resulting in whole or part from any User’s use of or reliance upon this system and the information contained within.\n" +
                            "The publisher and developer disclaim all warranties regarding such information whether express or implied, including any warranty as to the quality, accuracy, currency or suitability of this information for any particular purpose.\n" +
                            "By using the Pregnancy Dates Calculator, documentation and/or any software found therein, the User agrees to abide by United States and International copyright laws and all other applicable laws involving copyright.\n"
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
                    val months = arrayListOf("January", "February", "March","April", "May", "June", "July", "August", "September", "October", "November", "December")

                    val choice = Date(year - 1900, month -1, dayOfMonth)
                    val today = Date()
                    val difference = today.getTime() - choice.getTime()
                    val gAgeDaysTotsl = difference/ (1000 * 3600 * 24)
                    val gAgeWeeks = gAgeDaysTotsl/7.toDouble()
                    val gAgeWeeksRound = gAgeWeeks.toInt()
                    val gAgeDays = (gAgeDaysTotsl-(gAgeWeeksRound*7)).toInt()

                    val edd = Date(year - 1900, month -1,dayOfMonth + 280)
                    //var eddMonth = edd.month
                    val term = Date(year - 1900, month -1,dayOfMonth + 259)

                    val eddMInd = edd.month
                    val eddMName = months[eddMInd]
                    val eddYName = edd.year%100
                    val termMInd = term.month
                    val termMName = months[termMInd]
                    val termYName = term.year%100
                    val lmpMonthName = months[month-1]

                    lmpText.text = ("" + dayOfMonth + ". " + "$lmpMonthName" + " . " + year)
                    textAgeRight.text = ("$gAgeWeeksRound" + "  weeks" + "  $gAgeDays" + "  days" + "  = " + " $gAgeDaysTotsl" + " days")
                    textEDDRight.text = ("${edd.date}" + " . " + "$eddMName" + " . " + "20$eddYName")
                    textTermRight.text = ("${term.date}" + " . " + "$termMName" + " . " + "20$termYName")

                    if (gAgeDaysTotsl<=0 || gAgeDaysTotsl>295){
                        lmpText.text = ("Please, choose valid LMP")
                        textAgeRight.text = ("Please, choose valid LMP")
                        textEDDRight.text = ("Please, choose valid LMP")
                        textTermRight.text = ("Please, choose valid LMP")
                    }
                },
                year,
                month,
                day
            )
            dpd.show()
        }
    }
}