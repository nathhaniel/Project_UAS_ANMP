package com.example.project_uas.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.FragmentReportsBinding
import com.example.project_uas.repository.BudgetRepository
import com.example.project_uas.repository.ExpenseRepository
import com.example.project_uas.utils.SharedPreferencesManager
import com.example.project_uas.view.adapter.ReportAdapter
import kotlinx.coroutines.launch

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetRepository: BudgetRepository
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        val userId = SharedPreferencesManager.getUserId(context)
        val db = AppDatabase.getDatabase(context)

        budgetRepository = BudgetRepository(db.budgetDao())
        expenseRepository = ExpenseRepository(db.expenseDao())

        reportAdapter = ReportAdapter()
        binding.recyclerViewReport.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }

        // Observe budget list & total
        db.budgetDao().getAllBudgetsByUser(userId).observe(viewLifecycleOwner) { budgetList ->
            reportAdapter.submitList(budgetList)

            // Hitung total budget dan expense
            lifecycleScope.launch {
                val totalBudget = budgetRepository.getTotalBudgetByUser(userId) ?: 0
                val totalExpense = expenseRepository.getTotalExpenseByUser(userId) ?: 0
                val percentage = if (totalBudget > 0) {
                    (totalExpense * 100 / totalBudget)
                } else 0

                binding.textTotalBudget.text = "Total Budget: Rp$totalBudget"
                binding.textTotalExpense.text = "Total Expense: Rp$totalExpense"
                binding.textProgressPercent.text = "Progress: $percentage%"
                binding.progressBarTotal.progress = percentage
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
    