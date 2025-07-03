package com.example.project_uas.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.FragmentAddEditBudgetBinding
import com.example.project_uas.model.Budget
import com.example.project_uas.repository.BudgetRepository
import com.example.project_uas.utils.SharedPreferencesManager
import com.example.project_uas.viewmodel.BudgetViewModel
import com.example.project_uas.viewmodel.BudgetViewModelFactory

class AddEditBudgetFragment : Fragment() {

    private var _binding: FragmentAddEditBudgetBinding? = null
    private val binding get() = _binding!!

    private lateinit var budgetViewModel: BudgetViewModel
    private val args: AddEditBudgetFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dao = AppDatabase.getDatabase(requireContext()).budgetDao()
        val repository = BudgetRepository(dao)
        val factory = BudgetViewModelFactory(repository)
        budgetViewModel = ViewModelProvider(this, factory)[BudgetViewModel::class.java]

        val userId = SharedPreferencesManager.getUserId(requireContext())
        budgetViewModel.setUserId(userId)

        if (args.budgetId != 0) {
            budgetViewModel.getBudgetById(args.budgetId).observe(viewLifecycleOwner) { budget ->
                budget?.let {
                    binding.etBudgetName.setText(it.name)
                    binding.etBudgetNominal.setText(it.amount.toString())
                }
            }
        }

        binding.btnSaveBudget.setOnClickListener {
            val name = binding.etBudgetName.text.toString().trim()
            val amountText = binding.etBudgetNominal.text.toString().trim()

            if (name.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(requireContext(), "Semua data wajib diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toInt()

            val budget = Budget(
                id = args.budgetId,
                name = name,
                amount = amount,
                userId = userId
            )

            if (args.budgetId != 0) {
                budgetViewModel.update(budget)
            } else {
                budgetViewModel.insert(budget)
            }

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
