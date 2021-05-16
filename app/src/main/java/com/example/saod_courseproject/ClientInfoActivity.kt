package com.example.saod_courseproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

        info.text = "сим-карты пользователя"
        passport.text = intent.getStringExtra("passport")
        placeAndDate.text = intent.getStringExtra("place")
        name.text = intent.getStringExtra("name")
        yearOfBirth.text = intent.getStringExtra("year")
        address.text = intent.getStringExtra("address")


        MainList.hasFixedSize()
        MainList.layoutManager = LinearLayoutManager(this)
        MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIMByPassport(passport.text.toString()), this)
    }

    override fun onBackPressed() {
        ClientRecyclerAdapter.clearLists()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    fun onClickAdd(view: View) {
        if (currentList == 0) {
            ClientRecyclerAdapter.selectedSIM.forEach{ mobileOperator.addSIMForClient(passport.text.toString(), it.simNumber) }
            MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllFreeSIM(), this)
            ClientRecyclerAdapter.selectedSIM.clear()
        } else {
            currentList = 0
            info.text = "неиспользуемые сим-карты"
            ClientRecyclerAdapter.selectedSIM.clear()
            if (!ClientRecyclerAdapter.selectedInfo.isEmpty()) centreButtonChange(false) //
            MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllFreeSIM(), this)
            addButton.setImageResource(R.drawable.fill_plus_ic)
            mainMenu.setImageResource(R.drawable.menu_white_ic) //
            //removeButton.setImageResource(R.drawable.menu_white_ic)
        }
    }

    fun onClickRemove(view: View) {
        ClientRecyclerAdapter.selectedInfo.forEach { mobileOperator.removeSIMFromClient(it.simNumber) }
        ClientRecyclerAdapter.selectedInfo.clear()
        centreButtonChange(false)
        MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIMByPassport(passport.text.toString()), this)
    }
    fun onClickBack(view: View) {
        ClientRecyclerAdapter.clearLists()
        onBackPressed()
    }
    fun onClickMenu(view: View) {
        currentList = 1
        MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIMByPassport(passport.text.toString()), this)
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