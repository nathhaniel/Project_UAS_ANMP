package com.example.project_uas.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uas.R
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.FragmentExpenseListBinding
import com.example.project_uas.model.Expense
import com.example.project_uas.repository.BudgetRepository
import com.example.project_uas.repository.ExpenseRepository
import com.example.project_uas.utils.SharedPreferencesManager
import com.example.project_uas.view.adapter.ExpenseAdapter
import com.example.project_uas.viewmodel.BudgetViewModel
import com.example.project_uas.viewmodel.BudgetViewModelFactory
import com.example.project_uas.viewmodel.ExpenseViewModel
import com.example.project_uas.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ExpenseListFragment : Fragment() {

    private var _binding: FragmentExpenseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var budgetViewModel: BudgetViewModel
    private lateinit var adapter: ExpenseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext().applicationContext
        val db = AppDatabase.getDatabase(context)
        val userId = SharedPreferencesManager.getUserId(requireContext())

        expenseViewModel = ViewModelProvider(
            this,
            ExpenseViewModelFactory(ExpenseRepository(db.expenseDao()))
        )[ExpenseViewModel::class.java]

        budgetViewModel = ViewModelProvider(
            this,
            BudgetViewModelFactory(BudgetRepository(db.budgetDao()))
        )[BudgetViewModel::class.java]

        // Penting: Set userId untuk BudgetViewModel
        budgetViewModel.setUserId(userId)

        adapter = ExpenseAdapter(emptyList()) { expense ->
            // Tampilkan dialog detail saat item diklik
            budgetViewModel.getBudgetById(expense.budgetId).observe(viewLifecycleOwner) { budget ->
                val category = budget?.name ?: "Tanpa Kategori"
                showExpenseDetailDialog(expense, category)
            }
        }

        binding.recyclerViewExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewExpenses.adapter = adapter

        // Menampilkan semua pengeluaran user
        expenseViewModel.getAllExpenses(userId).observe(viewLifecycleOwner) { expenses ->
            Log.d("ExpenseListFragment", "Total expenses loaded: ${expenses.size}")
            adapter.setExpenses(expenses)
        }

        binding.fabAddExpense.setOnClickListener {
            findNavController().navigate(
                ExpenseListFragmentDirections.actionExpenseListFragmentToAddEditExpenseFragment(
                    budgetId = 0,
                    expenseId = 0
                )
            )
        }
    }

    private fun showExpenseDetailDialog(expense: Expense, categoryName: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_expense_detail, null)

        val tvDate = dialogView.findViewById<TextView>(R.id.tvDialogDate)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tvDialogDescription)
        val tvCategory = dialogView.findViewById<TextView>(R.id.tvDialogCategory)
        val tvAmount = dialogView.findViewById<TextView>(R.id.tvDialogAmount)
        val btnClose = dialogView.findViewById<Button>(R.id.btnDialogClose)

        val sdf = SimpleDateFormat("dd MMM yyyy HH.mm a", Locale.getDefault())
        tvDate.text = sdf.format(Date(expense.date))
        tvDesc.text = expense.description
        tvCategory.text = categoryName
        tvAmount.text = "IDR " + String.format("%,d", expense.amount).replace(',', '.')

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
