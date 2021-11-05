package com.skysam.hchirinos.rosqueteslucy.ui.sales

import androidx.lifecycle.*
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.repositories.CostumerRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.NoteSaleRepository
import com.skysam.hchirinos.rosqueteslucy.database.repositories.SalesRepository

class SalesViewModel : ViewModel() {

    val costumers: LiveData<MutableList<Costumer>> = CostumerRepository.getCostumers().asLiveData()
    val sales: LiveData<MutableList<Sale>> = SalesRepository.getSales().asLiveData()
    val notesSales: LiveData<MutableList<NoteSale>> = NoteSaleRepository.getNotesSale().asLiveData()
    val valueWeb: LiveData<String> = SalesRepository.getValueWeb().asLiveData()
    private val _indexPage = MutableLiveData<Int>()
    val indexPage: LiveData<Int> get() = _indexPage

    private val _costumer = MutableLiveData<Costumer>()
    val costumer: LiveData<Costumer> get() = _costumer

    private val _location = MutableLiveData<String>()
    val location: LiveData<String> get() = _location

    private val _addLocation = MutableLiveData<String?>().apply { value = null }
    val addLocation: LiveData<String?> get() = _addLocation

    private val _price = MutableLiveData<Double>()
    val price: LiveData<Double> get() = _price

    private val _rate = MutableLiveData<Double>()
    val rate: LiveData<Double> get() = _rate

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

    private val _textSearch = MutableLiveData<String>()
    val textSearch: LiveData<String> get() = _textSearch

    private val _isSale = MutableLiveData<Boolean>()
    val isSale: LiveData<Boolean> get() = _isSale

    fun addCostumer(costumer: Costumer) {
        _costumer.value = costumer
    }

    fun addLocation(id: String, location: String) {
        CostumerRepository.addLocation(id, location)
        _costumer.value!!.locations.add(_costumer.value!!.locations.size, location)
        _costumer.value = _costumer.value
        _addLocation.value = location
    }

    fun reviewInvoice(location: String, price: Double, rate: Double, quantity: Int,
                      isDolar: Boolean, invoice: Int, isPaid: Boolean, date: Long) {
        _location.value = location
        _price.value = price
        _rate.value = rate
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

    fun editSale(sale: Sale) {
        SalesRepository.editSale(sale)
    }

    fun annulSale(sale: Sale) {
        SalesRepository.annulSale(sale)
    }

    fun deleteSale(sale: Sale) {
        SalesRepository.deleteSale(sale)
    }

    fun addNoteSale(noteSale: NoteSale) {
        NoteSaleRepository.addNoteSale(noteSale)
    }

    fun changePage(index: Int) {
        _indexPage.value = index
    }

    fun updateBadge(number: Int) {
        _badge.value = number
    }

    fun newTextSearch(text: String) {
        _textSearch.value = text
    }

    fun changeIsSale(isSale: Boolean) {
        _isSale.value = isSale
    }
}