package com.example.saod_courseproject

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import data.*
import dataStructures.MyList
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recyclerview_clientitem.view.*
import java.util.*
import kotlin.concurrent.thread


class ClientRecyclerAdapter <T>(private val listT: MyList<T>, private val context: Context) :
    RecyclerView.Adapter<ClientRecyclerAdapter.ClientViewHolder>() {

    companion object {
        val selectedClient = MyList<Client>()
        val selectedSIM = MyList<SIM>()
        val selectedInfo = MyList<SIMInfo>()

        fun clearLists() {
            selectedSIM.clear()
            selectedInfo.clear()
            selectedClient.clear()
        }
    }
    init { clearLists() }

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val passport = itemView.findViewById<TextView>(R.id.passport)
        private val name = itemView.findViewById<TextView>(R.id.name)
        private val address = itemView.findViewById<TextView>(R.id.address)
        private val placeAndDate = itemView.findViewById<TextView>(R.id.placeAndDate)
        private val yearOfBirth = itemView.findViewById<TextView>(R.id.yearOfBirth)

        private var selected = false

        fun bind(item: Client, context: Context) {
            passport.text = item.passportNumber
            name.text = item.name
            address.text = item.address
            placeAndDate.text = item.placeAndDateOfIssueOfThePassport
            yearOfBirth.text = item.yearOfBirth.toString()

            clientOnClickListener(item, context)
        }
        fun bind(item: SIM, context: Context) {
            name.text = item.simNumber
            passport.text = item.tariff
            placeAndDate.text = item.yearOfIssue.toString()
            address.text = ""
            itemView.address.textSize = 0F
            with(item.isUse) { if (this) yearOfBirth.text = "Используется" else yearOfBirth.text = "Не используется" }

            simOnClickListener(item, context)
        }
        fun bind(item: SIMInfo, context: Context) {
            name.text = "Номер сим-карты: " + item.simNumber
            passport.text = "Номер паспорта: " + item.passportNumber
            placeAndDate.text = "Дата выдачи: " + item.dateOfIssue
            address.text = "Дата окончания: " + item.expirationDate
            yearOfBirth.text = "#" + (position + 1).toString()

            itemView.setOnClickListener {
                changeColor(Color.argb(32, 0, 0, 255))
                val s = selectedInfo.size //
                if (selectedInfo.contains(item)) selectedInfo.remove(item)
                else selectedInfo.add(item)
                if (selectedInfo.size == 1 && s == 0) (context as ClientInfoActivity).apply {
                    centreButtonChange(
                        true
                    )
                } //
                if (selectedInfo.size == 0 && s == 1) (context as ClientInfoActivity).apply {
                    centreButtonChange(
                        false
                    )
                } //
                it.isEnabled = false
                object : CountDownTimer(250, 50) {
                    override fun onTick(p0: Long) {}
                    override fun onFinish() {
                        it.isEnabled = true
                    }
                }.start()
            }
        }


        private fun clientOnClickListener(item: Client, context: Context) {
            itemView.setOnClickListener {
                if (selectedClient.isEmpty()) {
                    val intent = Intent(context, ClientInfoActivity::class.java).apply {
                        putExtra("passport", item.passportNumber)
                        putExtra("place", item.placeAndDateOfIssueOfThePassport)
                        putExtra("name", item.name)
                        putExtra("year", item.yearOfBirth.toString())
                        putExtra("address", item.address)
                    }
                    context.startActivity(intent)
                } else {
                    changeColor(Color.argb(64, 255, 0, 0))
                    clientSelectMain(item, context)
                }
            }
            itemView.setOnLongClickListener {
                changeColor(Color.argb(64, 255, 0, 0))
                clientSelectMain(item, context)
                return@setOnLongClickListener true
            }
        }
        private fun simOnClickListener(item: SIM, context: Context) {
            if (context is MainActivity) {
                itemView.setOnClickListener {
                    if (!selectedSIM.isEmpty()) {
                        changeColor(Color.argb(64, 255, 0, 0))
                        simSelectMain(item, context)
                    }
                }
                itemView.setOnLongClickListener {
                    changeColor(Color.argb(64, 255, 0, 0))
                    simSelectMain(item, context)
                    return@setOnLongClickListener true
                }
            } else {
                itemView.setOnClickListener {
                    changeColor(Color.argb(32, 0, 0, 255))
                    if (!selectedSIM.contains(item)) selectedSIM.add(item)
                    else selectedSIM.remove(item)
                }
            }
        }

        private fun clientSelectMain(item: Client, context: Context) {
            if (!selectedClient.contains(item)) selectedClient.add(item)
            else selectedClient.remove(item)
            (context as MainActivity).apply {
                if (selectedClient.isEmpty()) this.showTopMenu()
                else hideTopMenu()
            }
        }
        private fun simSelectMain(item: SIM, context: Context) {
            if (!selectedSIM.contains(item)) selectedSIM.add(item)
            else selectedSIM.remove(item)
            (context as MainActivity).apply {
                if (selectedSIM.isEmpty()) this.showTopMenu()
                else hideTopMenu()
            }
        }

        private fun changeColor(color: Int) {
            selected = !selected
            if (selected) itemView.setBackgroundColor(color)
            else itemView.setBackgroundColor(Color.argb(255, 50, 49, 49))
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_clientitem, parent, false)
        return ClientViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        when {
            listT[0] is Client -> holder.bind(listT[position] as Client, context)
            listT[0] is SIM -> holder.bind(listT[position] as SIM, context)
            listT[0] is SIMInfo -> holder.bind(listT[position] as SIMInfo, context)
        }
    }
    override fun getItemCount() = listT.size
}