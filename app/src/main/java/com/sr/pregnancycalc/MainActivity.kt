package com.sr.pregnancycalc

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
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

        // END of first time disclaimer //

        // create calender dialog and calculate the dates  //
        var lmpDates = ""
        /*val current = LocalDate.now()*/
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        pickDateBtn.setOnClickListener {

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    val month = month + 1
                    val lmpDate = LocalDate.of(year, month, dayOfMonth)
                    lmpDates = lmpDate.toString()
                    //println("this is lmpDates" + "$lmpDates")
                    //textView.setText("" + dayOfMonth + " " + month + ", " + year)
                    lmpText.text = ("" + dayOfMonth + ". " + month + ". " + year)


                    val date = LocalDate.now()
                    /*val day = date.dayOfMonth
                    val month = date.monthValue*/
                    val gAge = Period.between(
                        date,
                        lmpDate
                    )                              // gestational age in M and D

                    fun toWeeks() {
                        val gAgeWeeks =
                            (gAge.months * 30.5 + gAge.days) / 7 * -1            // gestational weeks
                        val gAgeWeeksRound =
                            gAgeWeeks.roundToInt()                     // round gestational weeks
                        val gAgeDays =
                            (gAge.months * 30.5 + gAge.days) % 7 * -1             // residual gestational days after extracting weeks MAIN !!!
                        val gAgeDaysRound =
                            gAgeDays.roundToInt()                        // round gestational days
                        val gAgeNomberOfMonth =
                            gAge.months                              // gestational months
                        val gAgeNomberOfDays =
                            gAge.days                                 // gestational days after extration of months

                        textAge.text = ("$gAgeWeeksRound" + "  weeks" + "  $gAgeDaysRound" + "   days")
                        println("gestational age is " + " $gAgeWeeksRound" + "  weeks" + "  $gAgeDaysRound" + "   days")
                        //println("residual days"+" $gAgeNomberOfDays")
                        //println("residual moths"+" $gAgeNomberOfMonth")
                    }
                    toWeeks()

                    fun eddCalc() {

                        val edd = lmpDate.plusDays(280)
                        val eddForm =
                            edd.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))  //formated
                        val edTermPregnancy = lmpDate.plusDays(259)
                        val edTermPregnancyForm =
                            edTermPregnancy.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        println("edd" + " $eddForm")
                        println("expected date of term pregnancy" + "   $edTermPregnancyForm")
                    }
                    eddCalc()


                    println("$date")
                    println("$day" + " and " + "$month")
                    println("$gAge")

                    //////////////////////////////////////////////////////////////////////////////////


                    //////////////////////////////////////////////////////////////////////////////////
                },
                year,
                month,
                day
            )
            dpd.show()


            //                 //


        }
    }
}