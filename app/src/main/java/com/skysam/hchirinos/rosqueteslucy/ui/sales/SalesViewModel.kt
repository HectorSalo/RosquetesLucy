package com.skysam.hchirinos.rosqueteslucy.ui.sales

import androidx.lifecycle.*
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class SalesViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()
    val sales: LiveData<MutableList<Sale>> = SalesRepository.getSales().asLiveData()
    private val _indexPage = MutableLiveData<Int>()
    val indexPage: LiveData<Int> get() = _indexPage

    private val _costumer = MutableLiveData<Costumer>()
    val costumer: LiveData<Costumer> get() = _costumer

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    private val _price = MutableLiveData<Double>()
    val price: LiveData<Double> get() = _price

    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> get() = _quantity

    private val _isDolar = MutableLiveData<Boolean>()
    val isDolar: LiveData<Boolean> get() = _isDolar

    private val _invoice = MutableLiveData<Int>()
    val invoice: LiveData<Int> get() = _invoice

    private val _isPaid = MutableLiveData<Boolean>()
    val isPaid: LiveData<Boolean> get() = _isPaid

    private val _date = MutableLiveData<Long>()
    val date: LiveData<Long> get() = _date

    private val _badge = MutableLiveData<Int>()
    val badge: LiveData<Int> get() = _badge

    fun addCostumer(costumer: Costumer) {
        _costumer.value = costumer
    }

    fun reviewInvoice(location: String, price: Double, quantity: Int,
                      isDolar: Boolean, invoice: Int, isPaid: Boolean, date: Long) {
        _location.value = location
        _price.value = price
        _quantity.value = quantity
        _isDolar.value = isDolar
        _invoice.value = invoice
        _isPaid.value = isPaid
        _date.value = date
    }

    fun addSale(sale: Sale) {
        SalesRepository.addSale(sale)
    }

    fun paidSale(sale: Sale) {
        SalesRepository.paidSale(sale)
    }

    fun changePage(index: Int) {
        _indexPage.value = index
    }

    fun updateBadge(number: Int) {
        _badge.value = number
    }
}