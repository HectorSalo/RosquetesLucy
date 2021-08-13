package com.skysam.hchirinos.rosqueteslucy.ui.sales

import androidx.lifecycle.*
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Location
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class SalesViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()
    val sale: LiveData<Sale> = SalesRepository.getSales().asLiveData()

    private val _sales = MutableLiveData<MutableList<Sale>>().apply {
        value = mutableListOf()
    }
    val sales: LiveData<MutableList<Sale>> get() = _sales

    private val _costumer = MutableLiveData<Costumer>()
    val costumer: LiveData<Costumer> get() = _costumer

    private val _location = MutableLiveData<Location>()
    val location: LiveData<Location> get() = _location

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

    fun reviewInvoice(costumer: Costumer, location: Location, price: Double, quantity: Int,
                      isDolar: Boolean, invoice: Int, isPaid: Boolean, date: Long) {
        _costumer.value = costumer
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

    fun addSaleToList(sale: Sale) {
        if (!_sales.value!!.contains(sale)) {
            _sales.value?.add(sale)
            _sales.value = _sales.value
        }
    }
}