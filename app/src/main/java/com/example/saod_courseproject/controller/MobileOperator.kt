package controller

import android.util.Log
import dataStructures.*
import data.*
import java.text.SimpleDateFormat
import java.util.*


class MobileOperator {

    private val clients = MyTreeMap<String, Client>()
    private val simList = MyHashMap<String, SIM>()
    private val simInfo = MyList<SIMInfo>()

    init {

        clients["1234-321000"] = Client("1234-321000", "Санкт-Петербург", "Михаил", 2001, "Переулок Бандитский")
        clients["1111-133722"] = Client("1111-133722", "Санкт-Петербург", "Александр", 1998, "Улица Разбитых фонарей")
        clients["3210-321111"] = Client("3210-321111", "Сыктывкар", "Сергей", 2001, "Проспект Просвящения")
        clients["1234-123345"] = Client("1234-123345", "Татарстан", "Рахим", 2001, "Татарский проспект")
        clients["2222-222222"] = Client("2222-222222", "Санкт-Петербург", "Евгений", 2001, "Улица Мира")

        simList["123-1234567"] = SIM("123-1234567", "Петербуржский андерграунд", 2013, true)
        simList["998-7654321"] = SIM("998-7654321", "Безлимитный", 2014, false)
        simList["321-1337228"] = SIM("321-1337228", "Сыктывкарская мафия", 2014, true)
        simList["123-7777777"] = SIM("123-7777777", "Татарский форсаж", 2019, true)
        simList["222-6666666"] = SIM("222-6666666", "Звонки без границ", 2010, true)
        simList["333-6666666"] = SIM("333-6666666", "Звонки с границами", 2010, true)
        simList["777-7777777"] = SIM("777-7777777", "Звонки за границами", 2010, false)

        simInfo.add(SIMInfo("1234-321000", "123-1234567", "12.12.2013", "12.12.2023"))
        simInfo.add(SIMInfo("3210-321111", "321-1337228", "10.10.2014", "10.10.2024"))
        simInfo.add(SIMInfo("1234-123345", "123-7777777", "22.06.2019", "22.06.2029"))
        simInfo.add(SIMInfo("2222-222222", "222-6666666", "01.01.2012", "01.01.2022"))
        simInfo.add(SIMInfo("2222-222222", "333-6666666", "01.01.2013", "01.01.2023"))
    }


    fun addNewClient(client: Client) = clients.put(client.passportNumber, client)

    fun removeClientFromService(passport: String) {

        val tmp = MyList<SIMInfo>().apply {
            simInfo.forEach {
                if (it.passportNumber == passport) {
                    simList[it.simNumber]?.isUse = false
                    this.add(it)
                }
            }
        }
        simInfo.removeAll(tmp)
        clients.remove(passport)
    }

    fun getAllClients() = MyList(clients.values)

    fun clearAllClients() {
        simInfo.clear()
        clients.clear()
        simList.forEach {
            it.value.isUse = false
        }
    }

    fun findClientByPassport(passport: String) = MyList<Client>().apply {
        clients.forEach {
            if (directSearch(passport, it.value.passportNumber))
                this.add(it.value)
        }
    }

    fun findClientByName(name: String) = MyList<Client>().apply {
        clients.forEach {
            if (directSearch(name, it.value.name))
                this.add(it.value)
        }
    }

    fun addSIM(sim: SIM) = simList.put(sim.simNumber, sim)

    fun removeSIM(simNumber: String) {
        val tmp = MyList<SIMInfo>().apply {
            simInfo.forEach { if (it.simNumber == simNumber) this.add(it) }
        }
        simInfo.removeAll(tmp)
        simList.remove(simNumber)
    }

    fun getAllSIM() = MyList(simList.values)

    fun clearAllSIM() {
        simInfo.clear()
        simList.clear()
    }

    fun findSIM(value: String) = MyList<SIM>().apply {
        simList.forEach {
            if (directSearch(value, it.value.tariff) || directSearch(value, it.value.simNumber))
                this.add(it.value)
        }
    }

    fun findClient(value: String) = MyList<Client>().apply {
        clients.forEach {
            if (directSearch(value, it.value.passportNumber) || directSearch(value, it.value.name) || directSearch(value, it.value.address))
                this.add(it.value)
        }
    }

    fun findSIMByNumber(simNumber: String) = MyList<SIM>().apply {
        simList.forEach {
            if (directSearch(simNumber, it.value.simNumber))
                this.add(it.value)
        }
    }

    fun findSIMByTariff(tariff: String) = MyList<SIM>().apply {
        simList.forEach {
            if (directSearch(tariff, it.value.tariff))
                this.add(it.value)
        }
    }

    fun findSIMByPassport(passport: String) = MyList<SIMInfo>().apply {
        simInfo.forEach {
            if (directSearch(passport, it.passportNumber))
                this.add(it)
        }
    }

    fun addSIMForClient(passport: String, simNumber: String) {
        val dateOfIssue = SimpleDateFormat("dd.MM.yyyy").format(Date()).toString()
        val expirationDate = SimpleDateFormat("dd.MM.yyyy").format(Date().time + 315576000000).toString()
        simList[simNumber]?.isUse = true
        simInfo.add(SIMInfo(passport, simNumber, dateOfIssue, expirationDate))
    }

    fun removeSIMFromClient(simNumber: String) {
        simList[simNumber]?.isUse = false
        simInfo.forEach { if (it.simNumber == simNumber) simInfo.remove(it) }
    }

    fun getAllFreeSIM() = MyList<SIM>().apply {
        simList.forEach {
            if (!it.value.isUse)
                this.add(it.value)
        }
    }

    private fun directSearch(find: String, line: String): Boolean {
        for (i in 0..line.length - find.length) {
            if (line.substring(i, i + find.length) == find) return true
        }
        return false
    }
}
