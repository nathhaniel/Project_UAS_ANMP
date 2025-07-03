package com.example.project_uas.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.project_uas.R
import com.example.project_uas.database.AppDatabase
import com.example.project_uas.databinding.FragmentProfileBinding
import com.example.project_uas.utils.SharedPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        setupListeners()

        return binding.root
    }

    private fun setupListeners() {
        // Tombol Sign Out
        binding.textSignOut.setOnClickListener {
            SharedPreferencesManager.clearSession(requireContext())
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }

        // Tombol Ganti Password
        binding.buttonChangePassword.setOnClickListener {
            val oldPass = binding.editOldPassword.text.toString()
            val newPass = binding.editNewPassword.text.toString()
            val repeatPass = binding.editRepeatPassword.text.toString()

            if (oldPass.isEmpty() || newPass.isEmpty() || repeatPass.isEmpty()) {
                Toast.makeText(requireContext(), "Semua kolom wajib diisi.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != repeatPass) {
                Toast.makeText(requireContext(), "Password baru tidak sama.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = SharedPreferencesManager.getUserId(requireContext())
            if (userId == -1) {
                Toast.makeText(requireContext(), "User tidak ditemukan.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {
                val userDao = AppDatabase.getDatabase(requireContext()).userDao()
                val user = userDao.getUserById(userId)

                if (user != null && user.password == oldPass) {
                    userDao.updatePassword(userId, newPass)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Password berhasil diubah!", Toast.LENGTH_SHORT).show()
                        binding.editOldPassword.text?.clear()
                        binding.editNewPassword.text?.clear()
                        binding.editRepeatPassword.text?.clear()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Password lama salah.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
