package com.example.saod_courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import data.Client
import dataStructures.MyList
import kotlinx.android.synthetic.main.activity_addclient.*

class AddClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addclient)
        passportTextChanged()
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }


    fun onClickAdd(view: View) {
        if (fieldsIsCorrect()) {
            val client = Client(
                passport.text.toString(),
                placeAndDate.text.toString(),
                name.text.toString(),
                yearOfBirth.text.toString().toInt(),
                address.text.toString()
            )
            mobileOperator.addNewClient(client)
            Toast.makeText(this, "Клиент ${client.name} добавлен", Toast.LENGTH_LONG).show()
            onBackPressed()
        } else {

            Toast.makeText(this, "Некорректные данные", Toast.LENGTH_SHORT).show()
            doge.animate().apply {
                duration = 500
                translationXBy(150F)

            }.withEndAction {
                doge.animate().apply {
                    startDelay = 500
                    duration = 500
                    translationXBy(-150F)
                }.start()
            }
            view.isEnabled = false
            object : CountDownTimer(2000, 250) {
                override fun onTick(p0: Long) {}
                override fun onFinish() {
                    view.isEnabled = true
                }
            }.start()
        }
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    private fun passportTextChanged() {
        passport.doAfterTextChanged {
            if (passport.text.length == 5 && !passport.text.contains("-")) passport.text.insert(4, "-")
        }
    }


    // удаление пробелов из имени, адреса, места
    // + выдача сообщения


    private fun fieldsIsCorrect(): Boolean {
        val fields = MyList<EditText>()
        if (name.text.isEmpty()) fields.add(name)
        if (yearOfBirth.text.isNotEmpty()) {
            if (yearOfBirth.text.toString().toInt() < 1900 || yearOfBirth.text.toString().toInt() > 2021) fields.add(yearOfBirth)
        } else fields.add(yearOfBirth)
        if (Regex("^\\d{4}-\\d{6}").find(passport.text) == null || !mobileOperator.findClientByPassport(passport.text.toString()).isEmpty()) fields.add(passport)
        if (placeAndDate.text.toString().replace(" ", "").isEmpty()) fields.add(placeAndDate)
        if (address.text.toString().replace(" ", "").isEmpty()) fields.add(address)
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