package com.example.project_uas.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uas.R
import com.example.project_uas.model.Budget

class BudgetAdapter(
    private var budgetList: List<Budget>,
    private val onItemClick: (Budget) -> Unit
) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBudgetName: TextView = itemView.findViewById(R.id.tvBudgetName)
        val tvBudgetAmount: TextView = itemView.findViewById(R.id.tvBudgetNominal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgetList[position]
        holder.tvBudgetName.text = budget.name
        holder.tvBudgetAmount.text = "IDR ${budget.amount}"

        holder.itemView.setOnClickListener {
            onItemClick(budget)
        }
    }

    override fun getItemCount(): Int = budgetList.size

    fun setBudgets(budgets: List<Budget>) {
        this.budgetList = budgets
        notifyDataSetChanged()
    }
}
