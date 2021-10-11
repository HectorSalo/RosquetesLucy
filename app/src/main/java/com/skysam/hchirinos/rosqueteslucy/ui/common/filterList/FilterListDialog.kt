package com.skysam.hchirinos.rosqueteslucy.ui.common.filterList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Pair
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogFilterListSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.ViewDetailsNoteSaleDialog
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages.NoteSaleAdapter
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages.OnClick
import com.skysam.hchirinos.rosqueteslucy.ui.sales.ViewDetailsSaleDialog
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.SalesAdapter
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 9/10/2021.
 */
class FilterListDialog(private val isSale: Boolean): DialogFragment(), OnClick,
    com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.OnClick {
    private var _binding: DialogFilterListSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FilterListViewModel by activityViewModels()
    private val costumers = mutableListOf<Costumer>()
    private val sales = mutableListOf<Sale>()
    private val salesResult = mutableListOf<Sale>()
    private val noteSale = mutableListOf<NoteSale>()
    private val noteSaleResult = mutableListOf<NoteSale>()
    private lateinit var adapterSale: SalesAdapter
    private lateinit var adapterNoteSale: NoteSaleAdapter
    private var dateStart: Date? = null
    private var dateFinal: Date? = null
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFilterListSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterSale = SalesAdapter(sales, this)
        adapterNoteSale = NoteSaleAdapter(noteSale, this)
        binding.rvSales.apply {
            setHasFixedSize(true)
            adapter = if (isSale) adapterSale else adapterNoteSale
        }
        binding.etDate.setOnClickListener { selecDate() }
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_date) {
                binding.tfDate.visibility = View.VISIBLE
                binding.linearNumber.visibility = View.GONE
                binding.tfDate.error = null
            } else {
                binding.tfDate.visibility = View.GONE
                binding.linearNumber.visibility = View.VISIBLE
                binding.tfFromNumber.error = null
                binding.tfUntilNumber.error = null
            }
        }
        binding.btnSearch.setOnClickListener {
            if (binding.rbDate.isChecked) {
                validateDate()
            } else {
                validateNumber()
            }
        }
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(viewLifecycleOwner, {
            costumers.clear()
            costumers.addAll(it)
        })
        viewModel.sales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                sales.clear()
                sales.addAll(it)
                if (isEditing) {
                    isEditing = false
                    if (binding.rbDate.isChecked) {
                        validateDate()
                    } else {
                        validateNumber()
                    }
                }
            }
        })
        viewModel.notesSales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                noteSale.clear()
                noteSale.addAll(it)
                if (isEditing) {
                    isEditing = false
                    if (binding.rbDate.isChecked) {
                        validateDate()
                    } else {
                        validateNumber()
                    }
                }
            }
        })
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val calendar = Calendar.getInstance()

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = selection.first
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            dateStart = calendar.time
            calendar.timeInMillis = selection.second
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            dateFinal = calendar.time
            binding.etDate.setText(getString(R.string.text_date_range,
                ClassesCommon.convertDateToString(dateStart!!),
                ClassesCommon.convertDateToString(dateFinal!!)))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun validateDate() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSearch.isEnabled = false
        binding.tfDate.error = null
        if (dateStart == null || dateFinal == null) {
            binding.tfDate.error = getString(R.string.error_date_invalidate)
            binding.progressBar.visibility = View.GONE
            binding.btnSearch.isEnabled = true
            return
        }
        var listIsEmpty = false
        val calendarStartRange = Calendar.getInstance()
        val calendarFinalRange = Calendar.getInstance()
        calendarStartRange.time = dateStart!!
        calendarFinalRange.time = dateFinal!!

        if (isSale) {
            salesResult.clear()
            for (sale in sales) {
                val dateSale = Date(sale.datePaid)
                if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                    salesResult.add(sale)
                }
            }
            adapterSale.updateList(salesResult)
            if (salesResult.isEmpty()) listIsEmpty = true
        } else {
            noteSaleResult.clear()
            for (note in noteSale) {
                val dateNoteSale = Date(note.datePaid)
                if (dateNoteSale.after(calendarStartRange.time) && dateNoteSale.before(calendarFinalRange.time)) {
                    noteSaleResult.add(note)
                }
            }
            adapterNoteSale.updateList(noteSaleResult)
            if (noteSaleResult.isEmpty()) listIsEmpty = true
        }
        validateListEmpty(listIsEmpty)
    }

    private fun validateNumber() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSearch.isEnabled = false
        binding.tfFromNumber.error = null
        binding.tfUntilNumber.error = null

        val numberInit = binding.etFromNumber.text.toString().trim()
        val numberFinal = binding.etUntilNumber.text.toString().trim()

        if (numberInit.isEmpty()) {
            binding.tfFromNumber.error = getString(R.string.error_field_empty)
            binding.etFromNumber.requestFocus()
            binding.progressBar.visibility = View.GONE
            binding.btnSearch.isEnabled = true
            return
        }
        val numberInitInt = numberInit.toInt()
        if (numberFinal.isEmpty()) {
            binding.tfUntilNumber.error = getString(R.string.error_field_empty)
            binding.etUntilNumber.requestFocus()
            binding.progressBar.visibility = View.GONE
            binding.btnSearch.isEnabled = true
            return
        }
        val numberFinalInt = numberFinal.toInt()
        if (numberFinalInt < numberInitInt) {
            binding.tfUntilNumber.error = getString(R.string.error_number_final)
            binding.etUntilNumber.requestFocus()
            binding.progressBar.visibility = View.GONE
            binding.btnSearch.isEnabled = true
            return
        }
        Keyboard.close(binding.root)
        var listIsEmpty = false
        if (isSale) {
            salesResult.clear()
            for (sale in sales) {
                val numberSale = sale.invoice
                if (numberSale in numberInitInt..numberFinalInt) salesResult.add(sale)
            }
            adapterSale.updateList(salesResult)
            if (salesResult.isEmpty()) listIsEmpty = true
        } else {
            noteSaleResult.clear()
            for (note in noteSale) {
                val numberNoteSale = note.noteNumber
                if (numberNoteSale in numberInitInt..numberFinalInt) noteSaleResult.add(note)
            }
            adapterNoteSale.updateList(noteSaleResult)
            if (noteSaleResult.isEmpty()) listIsEmpty = true
        }
        validateListEmpty(listIsEmpty)
    }

    private fun validateListEmpty(listIsEmpty: Boolean) {
        binding.progressBar.visibility = View.GONE
        binding.btnSearch.isEnabled = true
        if (listIsEmpty) {
            binding.rvSales.visibility = View.GONE
            binding.textListEmpty.text = getString(R.string.list_filter_without_result)
            binding.textListEmpty.visibility = View.VISIBLE
            return
        }
        binding.rvSales.visibility = View.VISIBLE
        binding.textListEmpty.visibility = View.GONE
    }

    override fun viewNoteSale(noteSale: NoteSale) {
        for (cos in costumers) {
            if (cos.id == noteSale.idCostumer) {
                noteSale.idCostumer = cos.identifier
            }
        }
        if (!noteSale.isPaid) isEditing = true
        val viewDetailsNoteSaleDialog = ViewDetailsNoteSaleDialog(noteSale)
        viewDetailsNoteSaleDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteNoteSale(noteSale: NoteSale) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                isEditing = true
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteNoteSale(noteSale)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun viewSale(sale: Sale) {
        for (cos in costumers) {
            if (cos.id == sale.idCostumer) {
                sale.idCostumer = cos.identifier
            }
        }
        if (!sale.isPaid) isEditing = true
        val viewDetailsSale = ViewDetailsSaleDialog(sale)
        viewDetailsSale.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteSale(sale: Sale) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                isEditing = true
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteSale(sale)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }
}