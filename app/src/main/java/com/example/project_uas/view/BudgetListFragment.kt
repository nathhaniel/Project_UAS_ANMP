package com.example.project_uas.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.FragmentBudgetListBinding
import com.example.project_uas.repository.BudgetRepository
import com.example.project_uas.utils.SharedPreferencesManager
import com.example.project_uas.view.adapter.BudgetAdapter
import com.example.project_uas.viewmodel.BudgetViewModel
import com.example.project_uas.viewmodel.BudgetViewModelFactory

class BudgetListFragment : Fragment() {

    private var _binding: FragmentBudgetListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BudgetViewModel
    private lateinit var adapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val dao = AppDatabase.getDatabase(requireContext()).budgetDao()
        val repository = BudgetRepository(dao)
        val factory = BudgetViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BudgetViewModel::class.java]

        val userId = SharedPreferencesManager.getUserId(requireContext())
        viewModel.setUserId(userId)

        adapter = BudgetAdapter(emptyList()) { budget ->
            val action = BudgetListFragmentDirections
                .actionBudgetListFragmentToAddEditBudgetFragment(budget.id)
            findNavController().navigate(action)
        }

        binding.recyclerViewBudget.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewBudget.adapter = adapter

        viewModel.allBudgets.observe(viewLifecycleOwner) { budgets ->
            adapter.setBudgets(budgets)
        }

        binding.fabAddBudget.setOnClickListener {
            findNavController().navigate(
                BudgetListFragmentDirections.actionBudgetListFragmentToAddEditBudgetFragment(0)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
