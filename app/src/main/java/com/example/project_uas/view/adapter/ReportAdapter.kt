package com.example.project_uas.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.ItemReportBudgetBinding
import com.example.project_uas.model.Budget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportAdapter : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private var budgets: List<Budget> = emptyList()

    fun submitList(list: List<Budget>) {
        budgets = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemReportBudgetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(budgets[position])
    }

    override fun getItemCount(): Int = budgets.size

    inner class ReportViewHolder(private val binding: ItemReportBudgetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(budget: Budget) {
            val context = binding.root.context
            val db = AppDatabase.getDatabase(context)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val expenseDao = db.expenseDao()
                    val rawExpense = expenseDao.getTotalExpenseByBudgetId(budget.id)
                    val totalExpense = rawExpense ?: 0

                    val maxBudget = budget.amount
                    val remaining = maxBudget - totalExpense
                    val progress = if (maxBudget > 0) (totalExpense * 100 / maxBudget) else 0

                    withContext(Dispatchers.Main) {
                        binding.textBudgetName.text = budget.name
                        binding.textMaxBudget.text = "Max: Rp$maxBudget"
                        binding.textUsedBudget.text = "Used: Rp$totalExpense"
                        binding.textRemainingBudget.text = "Remaining: Rp$remaining"
                        binding.progressBar.progress = progress
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        binding.textBudgetName.text = budget.name
                        binding.textMaxBudget.text = "Max: Rp${budget.amount}"
                        binding.textUsedBudget.text = "Used: Rp0"
                        binding.textRemainingBudget.text = "Remaining: Rp${budget.amount}"
                        binding.progressBar.progress = 0
                    }
                }
            }
        }
    }
}
