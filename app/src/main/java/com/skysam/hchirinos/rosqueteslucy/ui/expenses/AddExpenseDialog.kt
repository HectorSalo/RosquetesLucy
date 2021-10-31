package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.ui.common.ExitDialog
import com.skysam.hchirinos.rosqueteslucy.ui.common.OnClickExit
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddExpenseBinding
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
class AddExpenseDialog(private val supplier: Supplier): DialogFragment(),
    OnClickList, OnClickExit, TextWatcher {
    private var _binding: DialogAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var adapterItem: ItemListAdapter
    private var dateSelected: Long = 0
    private val allProducts = mutableListOf<String>()
    private val productsInList = mutableListOf<PrimaryProducts>()
    private lateinit var valueWeb: String
    private var total = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        adapterItem = ItemListAdapter(productsInList, this)
        binding.rvList.apply {
            setHasFixedSize(true)
            adapter = adapterItem
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
        binding.etRate.addTextChangedListener(this)
        binding.tvNameSupplier.text = supplier.name
        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etDate.setOnClickListener { selecDate() }

        binding.fabCancel.setOnClickListener {
            getOut()
        }

        binding.fabSave.setOnClickListener { validateData() }

        binding.tfSearchProducts.setStartIconOnClickListener {
            val addProduct = AddProductDialog(binding.etSarchProduct.text.toString().trim(), allProducts)
            addProduct.show(requireActivity().supportFragmentManager, tag)
        }

        binding.etSarchProduct.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Keyboard.close(binding.root)
            var exists = false
            val nameSelected = parent.getItemAtPosition(position).toString()
            for (prod in productsInList) {
                if (prod.name == nameSelected){
                    exists = true
                    Toast.makeText(requireContext(), getString(R.string.product_added), Toast.LENGTH_SHORT).show()
                    binding.rvList.scrollToPosition(position)
                    break
                }
            }
            if (!exists) {
                val product = PrimaryProducts(
                    nameSelected,
                    Constants.PRODUCT_UNIT_INITIAL,
                    0.0,
                    1.0
                )
                viewModel.addProductInList(product)
            }
        }

        loadViewModel()
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.valueWeb.observe(viewLifecycleOwner, {
            if (_binding != null) {
                valueWeb = it
                binding.tfRate.hint = getString(R.string.text_rate)
                binding.etRate.setText(it)
                if (it == "1,00") {
                    binding.tfRate.error = getString(R.string.error_rate)
                    binding.etRate.doAfterTextChanged { binding.tfRate.error = null }
                }
            }
        })
        viewModel.allProducts.observe(viewLifecycleOwner, {
            if (_binding != null) {
                allProducts.clear()
                allProducts.addAll(it)
                val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, allProducts.sorted())
                binding.etSarchProduct.setAdapter(adapterSearchProduct)
            }
        })
        viewModel.productsInList.observe(viewLifecycleOwner, {
            if (_binding != null) {
                productsInList.clear()
                productsInList.addAll(it)
                adapterItem.updateList(productsInList)
            }
        })
        viewModel.priceTotal.observe(viewLifecycleOwner, {
            if (_binding != null) {
                total = it
                binding.tvTotal.text = getString(R.string.text_total_dolar_expense,
                ClassesCommon.convertDoubleToString(it))
            }
        })
    }

    private fun validateData() {
        binding.tfDate.error = null

        val dateSelectedS = binding.etDate.text.toString()
        if (dateSelectedS.isEmpty()) {
            binding.tfDate.error = getString(R.string.error_field_empty)
            binding.etDate.requestFocus()
            return
        }
        var rate = binding.etRate.text.toString()
        if (rate.isEmpty()) {
            binding.tfRate.error = getString(R.string.error_field_empty)
            binding.etRate.requestFocus()
            return
        }
        if (rate == "0,00") {
            binding.tfRate.error = getString(R.string.error_price_zero)
            binding.etRate.requestFocus()
            return
        }
        rate = rate.replace(".", "").replace(",", ".")

        if (total == 0.0) {
            Toast.makeText(requireContext(), getString(R.string.error_price_zero), Toast.LENGTH_SHORT).show()
            return
        }

        val expense = Expense(
            "",
            supplier.name,
            supplier.id,
            productsInList,
            total,
            dateSelected,
            rate.toDouble()
        )
        viewModel.addExpense(expense)
        viewModel.clearFields()
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val calendar = Calendar.getInstance()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Long? ->
            calendar.timeInMillis = selection!!
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = calendar.timeInMillis + offset
            val dateSelec = calendar.time
            dateSelected = dateSelec.time
            binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    override fun deleteItem(primaryProducts: PrimaryProducts) {
        viewModel.removeProductInList(primaryProducts)
    }

    override fun editItem(primaryProducts: PrimaryProducts) {
        val postion = productsInList.indexOf(primaryProducts)
        val rate = valueWeb.replace(".", "").replace(",", ".")
        val editProductDialog = EditProductDialog(primaryProducts, postion, rate.toDouble())
        editProductDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        viewModel.clearFields()
        dialog?.dismiss()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = ClassesCommon.convertDoubleToString(cantidad)

        if (s.toString() == binding.etRate.text.toString()) {
            binding.etRate.removeTextChangedListener(this)
            binding.etRate.setText(cadena)
            binding.etRate.setSelection(cadena.length)
            binding.etRate.addTextChangedListener(this)
            valueWeb = cadena
        }
    }
}