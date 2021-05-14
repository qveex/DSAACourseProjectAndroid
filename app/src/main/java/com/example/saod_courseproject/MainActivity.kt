package com.example.saod_courseproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import controller.MobileOperator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*


val mobileOperator = MobileOperator() // все мы не без греха

class MainActivity : AppCompatActivity() {

    companion object { var currentMenu = 1 }
    private var topMenu = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MainList.hasFixedSize()
        MainList.layoutManager = LinearLayoutManager(this)
        listFilling()
        searchTextChanged()
    }

    private fun searchTextChanged() {
        search.doAfterTextChanged {
            when (currentMenu) {
                0 -> MainList.adapter = ClientRecyclerAdapter(mobileOperator.findClient(it.toString()), this)
                1 -> MainList.adapter = ClientRecyclerAdapter(mobileOperator.findSIM(it.toString()), this)
            }
        }
    }

    fun onClickAdd(view: View) {
        val intent = if (currentMenu == 0) Intent(this, AddClientActivity::class.java) else Intent(this, AddSIMActivity::class.java)
        startActivity(intent)
        onBackPressed()
    }
    fun onClickClient(view: View) {
        showTopMenu()
        clientButton.setImageResource(R.drawable.fill_client_ic)
        simButton.setImageResource(R.drawable.sim_ic)
        search.setText("")
        currentMenu = 0

        MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllClients(), this)
    }
    fun onClickSim(view: View) {
        showTopMenu()
        simButton.setImageResource(R.drawable.fill_sim_ic)
        clientButton.setImageResource(R.drawable.client_ic)
        search.setText("")
        currentMenu = 1

        MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllSIM(), this)
    }
    fun onClickRemove(view: View) {
        ClientRecyclerAdapter.selectedClient.forEach {
            mobileOperator.removeClientFromService(it.passportNumber)
        }
        listFilling()
        showTopMenu()
    }
    fun onClickRemoveAll(view: View) {
        mobileOperator.clearAllClients()
        listFilling()
    }


    private fun listFilling() {
        when (currentMenu) {
            0 -> {
                MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllClients(), this)
                clientButton.setImageResource(R.drawable.fill_client_ic)
            }
            1 -> {
                MainList.adapter = ClientRecyclerAdapter(mobileOperator.getAllSIM(), this)
                simButton.setImageResource(R.drawable.fill_sim_ic)
            }
        }
    }
    fun hideTopMenu() {
        if (topMenu) {
            topMenu = !topMenu
            search.animate().apply {
                duration = 300
                translationYBy(-150F)
            }
            searchImage.animate().apply {
                duration = 300
                translationYBy(-150F)
            }


            removeAllButton.animate().apply {
                duration = 300
                translationYBy(110F)
            }
            removeButton.animate().apply {
                duration = 300
                translationYBy(90F)
            }
        }
    }
    fun showTopMenu() {
        if (!topMenu) {
            topMenu = !topMenu
            search.animate().apply {
                duration = 300
                translationYBy(150F)
            }
            searchImage.animate().apply {
                duration = 300
                translationYBy(150F)
            }

            removeAllButton.animate().apply {
                duration = 300
                translationYBy(-110F)
            }
            removeButton.animate().apply {
                duration = 300
                translationYBy(-90F)
            }
        }
    }
}