package com.example.project_uas.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.FragmentAddEditExpenseBinding
import com.example.project_uas.model.Budget
import com.example.project_uas.model.Expense
import com.example.project_uas.repository.BudgetRepository
import com.example.project_uas.repository.ExpenseRepository
import com.example.project_uas.utils.SharedPreferencesManager
import com.example.project_uas.viewmodel.BudgetViewModel
import com.example.project_uas.viewmodel.BudgetViewModelFactory
import com.example.project_uas.viewmodel.ExpenseViewModel
import com.example.project_uas.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddEditExpenseFragment : Fragment() {

    private var _binding: FragmentAddEditExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    private val args: AddEditExpenseFragmentArgs by navArgs()
    private var selectedDate: Long = System.currentTimeMillis()
    private var selectedBudget: Budget? = null
    private var maxBudget = 0
    private var currentExpense = 0
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = AppDatabase.getDatabase(requireContext())
        budgetViewModel = ViewModelProvider(this, BudgetViewModelFactory(BudgetRepository(db.budgetDao())))
            .get(BudgetViewModel::class.java)
        expenseViewModel = ViewModelProvider(this, ExpenseViewModelFactory(ExpenseRepository(db.expenseDao())))
            .get(ExpenseViewModel::class.java)

        userId = SharedPreferencesManager.getUserId(requireContext())
        budgetViewModel.setUserId(userId)

        val expenseId = args.expenseId
        val passedBudgetId = args.budgetId

        setupDatePicker()
        setupBudgetSpinner(passedBudgetId)

        if (expenseId != 0) {
            expenseViewModel.getExpensesByBudgetId(passedBudgetId).observe(viewLifecycleOwner) { list ->
                val expense = list.find { it.id == expenseId }
                if (expense != null) {
                    binding.editNominal.setText(expense.amount.toString())
                    binding.editNote.setText(expense.description)
                    selectedDate = expense.date
                    binding.textDate.text = formatDate(selectedDate)
                }
            }
        }

        binding.btnAddExpense.setOnClickListener {
            val nominalText = binding.editNominal.text.toString()
            val description = binding.editNote.text.toString()
            val selectedBudgetName = binding.spinnerBudget.text.toString()
            val chosenBudget = budgetViewModel.allBudgets.value?.find {
                it.name == selectedBudgetName && it.userId == userId
            }

            if (chosenBudget == null) {
                showToast("Pilih budget terlebih dahulu")
                return@setOnClickListener
            }

            if (nominalText.isBlank()) {
                showToast("Nominal tidak boleh kosong")
                return@setOnClickListener
            }

            val nominal = nominalText.toIntOrNull()
            if (nominal == null || nominal <= 0) {
                showToast("Nominal tidak valid")
                return@setOnClickListener
            }

            val sisa = maxBudget - currentExpense
            if (expenseId == 0 && nominal > sisa) {
                showToast("Pengeluaran melebihi sisa budget")
                return@setOnClickListener
            }

            val newExpense = Expense(
                id = if (expenseId == 0) 0 else expenseId,
                budgetId = chosenBudget.id,
                amount = nominal,
                description = description,
                date = selectedDate,
                userId = userId
            )

            expenseViewModel.insert(newExpense)
            findNavController().popBackStack()
        }
    }

    private fun setupBudgetSpinner(passedBudgetId: Int) {
        budgetViewModel.allBudgets.observe(viewLifecycleOwner) { allBudgets ->
            val budgets = allBudgets.filter { it.userId == userId }
            if (budgets.isEmpty()) return@observe

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                budgets.map { it.name }
            )
            binding.spinnerBudget.setAdapter(adapter)

            val defaultBudget = budgets.find { it.id == passedBudgetId }
            if (defaultBudget != null) {
                binding.spinnerBudget.setText(defaultBudget.name, false)
                selectedBudget = defaultBudget
                maxBudget = defaultBudget.amount

                budgetViewModel.getTotalExpenseByBudgetId(defaultBudget.id).observe(viewLifecycleOwner) { total ->
                    currentExpense = total ?: 0
                    updateProgress()
                }
            }

            binding.spinnerBudget.setOnItemClickListener { _, _, position, _ ->
                selectedBudget = budgets[position]
                maxBudget = selectedBudget!!.amount

                budgetViewModel.getTotalExpenseByBudgetId(selectedBudget!!.id).observe(viewLifecycleOwner) { total ->
                    currentExpense = total ?: 0
                    updateProgress()
                }
            }
        }
    }

    private fun setupDatePicker() {
        binding.textDate.text = formatDate(selectedDate)
        binding.textDate.setOnClickListener {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            val dialog = DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    calendar.set(y, m, d)
                    selectedDate = calendar.timeInMillis
                    binding.textDate.text = formatDate(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }
    }

    private fun updateProgress() {
        val remaining = maxBudget - currentExpense
        binding.progressBar.max = maxBudget
        binding.progressBar.progress = currentExpense
        binding.editNominal.hint = "Nominal (sisa Rp $remaining)"
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
