package com.example.saod_courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import data.SIM
import dataStructures.MyList
import kotlinx.android.synthetic.main.activity_add_sim.*

class AddSIMActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_sim)
        simTextChanged()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun onClickAdd(view: View) {
        if (fieldsIsCorrect()) {
            val sim = SIM(
                simNumber.text.toString(),
                tariff.text.toString(),
                yearOfIssue.text.toString().toInt(),
            )
            mobileOperator.addSIM(sim)
            Toast.makeText(this, "Сим-карта ${sim.simNumber} добавлена", Toast.LENGTH_LONG).show()
            onBackPressed()
        } else {
            Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show()
        }
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    private fun simTextChanged() {
        simNumber.doAfterTextChanged {
            if (simNumber.text.length == 4 && !simNumber.text.contains("-")) simNumber.text.insert(3, "-")
        }
    }

    private fun fieldsIsCorrect(): Boolean {


        // запретить ввод чисел > или < пограничных значений (год)
        // + сообщение о некоректности


        val fields = MyList<EditText>()
        if (yearOfIssue.text.toString().length != 4 || yearOfIssue.text.toString().toInt() < 1991 || yearOfIssue.text.toString().toInt() > 2021) fields.add(yearOfIssue)
        if (tariff.text.toString().replace(" ", "").isEmpty()) fields.add(tariff)
        if (Regex("^\\d{3}-\\d{7}").find(simNumber.text) == null || !mobileOperator.findSIMByNumber(simNumber.text.toString()).isEmpty()) fields.add(simNumber)
        return if (fields.isEmpty()) true
        else {
            highlightIncorrectFields(fields)
            false
        }
    }
    private fun highlightIncorrectFields(fields: MyList<EditText>) {
        fields.forEach { it.setBackgroundColor(resources.getColor(R.color.incorrectInformationBackground)) }
        val timer = object: CountDownTimer(3000, 1000) {
            override fun onTick(p0: Long) { }
            override fun onFinish() { fields.forEach { it.setBackgroundColor(resources.getColor(R.color.hintBackgroundColor)) } }
        }
        timer.start()
    }
}