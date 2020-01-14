package com.sr.pregnancycalc

import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //   first time disclaimer //

        val prefs: SharedPreferences = getSharedPreferences ("prefs", MODE_PRIVATE)
        var firstStart = prefs.getBoolean("firstStart", true)
        val editor = prefs.edit()
        if (firstStart){
            val builder = android.app.AlertDialog.Builder(this)
                .setMessage("All information contained in and produced by the Pregnancy Dates Calculator is provided for educational purposes only. This information should not be used for the diagnosis or treatment of any health problem or disease. This information is not intended to replace clinical judgment or guide individual patient care in any manner.\n" +
                        "The User is hereby notified that the information contained herein may not meet the user’s needs.\n" +
                        "The User of this software assumes sole responsibility for any decisions made or actions taken based on the information contained in the application.\n" +
                        "Neither the system’s authors nor any other party involved in the preparation, publication or distribution of the Pregnancy Dates Calculator shall be liable for any special, consequential, or exemplary damages resulting in whole or part from any User’s use of or reliance upon this system and the information contained within.\n" +
                        "The publisher and developer disclaim all warranties regarding such information whether express or implied, including any warranty as to the quality, accuracy, currency or suitability of this information for any particular purpose.\n" +
                        "By using the Pregnancy Dates Calculator, documentation and/or any software found therein, the User agrees to abide by United States and International copyright laws and all other applicable laws involving copyright.\n")
                .setPositiveButton("Agree and Proceed", { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                    editor.putBoolean ("firstStart",false)
                    editor.apply()

                })
                .setNegativeButton("EXIT",{ dialogInterface: DialogInterface, i: Int ->
                    finish()
                })
                .show()
        }

        // END of first time disclaimer //

    }
}
