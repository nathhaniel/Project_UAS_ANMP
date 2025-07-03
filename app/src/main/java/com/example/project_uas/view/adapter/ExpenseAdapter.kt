package com.example.project_uas.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_uas.databinding.ItemExpenseBinding
import com.example.project_uas.model.Expense
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private var expenses: List<Expense>,
    private val onItemClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        with(holder.binding) {
            // Format tanggal
            val dateFormat = SimpleDateFormat("dd MMM yyyy HH.mm", Locale("in", "ID"))
            tvExpenseDate.text = dateFormat.format(Date(expense.date))

            // Deskripsi (opsional)
            tvExpenseDescription.text = expense.description.ifBlank { "-" }

            // Format nominal ke IDR
            val formattedAmount = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                .format(expense.amount)
                .replace("Rp", "IDR")
                .replace(",00", "")
            tvExpenseAmount.text = formattedAmount

            // Klik item -> tampilkan dialog detail
            root.setOnClickListener {
                onItemClick(expense)
            }
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun setExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
