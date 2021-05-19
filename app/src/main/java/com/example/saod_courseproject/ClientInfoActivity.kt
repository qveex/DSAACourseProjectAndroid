package com.example.saod_courseproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_client_info.*
import kotlinx.android.synthetic.main.main_content.*

class ClientInfoActivity : AppCompatActivity() {

    private var currentList = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_info)

        info.text = "сим-карты клиента"
        passport.text = getString(R.string.passport, intent.getStringExtra("passport"))
        placeAndDate.text = getString(R.string.placeAndDate, intent.getStringExtra("place"))
        name.text = getString(R.string.name, intent.getStringExtra("name"))
        yearOfBirth.text = getString(R.string.yearOfBirth, intent.getStringExtra("year"))
        address.text = getString(R.string.address, intent.getStringExtra("address"))


        MainList.hasFixedSize()
        MainList.layoutManager = LinearLayoutManager(this)
        MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIMByPassport(passport.text.toString().removePrefix("Паспорт: ")), this)
    }

    override fun onBackPressed() {
        ClientRecyclerAdapter.clearLists()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun onClickAdd(view: View) {
        if (currentList == 0) {
            ClientRecyclerAdapter.selectedSIM.forEach{ mobileOperator.addSIMForClient(passport.text.toString().removePrefix("Паспорт: "), it.simNumber) }
            MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllFreeSIM(), this)
            Toast.makeText(this, "Сим-карта(ы) привязана(ы)", Toast.LENGTH_SHORT).show()
            ClientRecyclerAdapter.selectedSIM.clear()
        } else {
            currentList = 0
            info.text = "неиспользуемые сим-карты"
            ClientRecyclerAdapter.selectedSIM.clear()
            if (!ClientRecyclerAdapter.selectedInfo.isEmpty()) centreButtonChange(false) //
            MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllFreeSIM(), this)
            addButton.setImageResource(R.drawable.fill_plus_ic)
            mainMenu.setImageResource(R.drawable.menu_white_ic) //
        }
    }

    fun onClickRemove(view: View) {

        // спросить об удалении

        ClientRecyclerAdapter.selectedInfo.forEach { mobileOperator.removeSIMFromClient(it.simNumber) }
        Toast.makeText(this, "Сим-карта(ы) возвращена(ы)", Toast.LENGTH_SHORT).show()
        ClientRecyclerAdapter.selectedInfo.clear()
        centreButtonChange(false)
        MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIMByPassport(passport.text.toString().removePrefix("Паспорт: ")), this)
    }
    fun onClickBack(view: View) {
        ClientRecyclerAdapter.clearLists()
        onBackPressed()
    }
    fun onClickMenu(view: View) {
        currentList = 1
        info.text = "сим-карты клиента"
        MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIMByPassport(passport.text.toString().removePrefix("Паспорт: ")), this)
        addButton.setImageResource(R.drawable.plus_ic)
        mainMenu.setImageResource(R.drawable.fill_menu_white_ic)

        /* зачем? */

        removeButton.setImageResource(R.drawable.delete_ic)
        removeButton.isVisible = false
        removeButton.isVisible = true
        Thread.sleep(1)
        removeButton.isVisible = false
        removeButton.isVisible = true
        removeButton.setImageResource(R.drawable.delete_ic)
    }


    fun centreButtonChange(remove: Boolean) {
        if (remove) {
            removeButton.animate().apply {
                duration = 250
                translationYBy(-140F)
            }
            mainMenu.animate().apply {
                duration = 250
                translationYBy(210F)
            }
        } else {
            removeButton.animate().apply {
                duration = 250
                translationYBy(140F)
            }
            mainMenu.animate().apply {
                duration = 250
                translationYBy(-210F)
            }
        }
    }
}