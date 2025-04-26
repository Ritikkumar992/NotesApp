package com.example.notesapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.notesapp.R
import com.example.notesapp.databinding.FragmentRegisterBinding
import com.example.notesapp.models.UserRequest
import com.example.notesapp.utils.NetworkResult
import com.example.notesapp.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding:FragmentRegisterBinding;
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = FragmentRegisterBinding.inflate(inflater, container,false)

        if(tokenManager.getToken() != null)
        {
            findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }

        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            //authViewModel.loginUser(UserRequest("ritik@gmail.com", "1111", "ritik"))
        }
        binding.btnSignUp.setOnClickListener {
            //findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
            val validationResult = validateUserInput()
            if(validationResult.first)
            {
                authViewModel.registerUser(getUserRequest()) // authViewModel.registerUser(UserRequest("ritik@gmail.com", "1111", "ritik"))
            }
            else{
                binding.txtError.text = validationResult.second
            }
        }

        bindObserver();

    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val userRequest = getUserRequest();
        return authViewModel.validateCredentials(userRequest.username, userRequest.email, userRequest.password, false)
    }

    private fun getUserRequest():UserRequest{
        val emailAddress = binding.txtEmail.text.toString();
        val password = binding.txtPassword.text.toString()
        val userName = binding.txtUsername.text.toString();

        return UserRequest(emailAddress, password, userName)
    }

    private fun bindObserver() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when(it)
            {
                is NetworkResult.Success ->{
                    // token
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
                }
                is NetworkResult.Error ->{
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading ->{
                    binding.progressBar.isVisible = true
                }
                else -> {}
            }
        })
    }


    // to improve perform.
    override fun onDestroyView() {
        super.onDestroyView()
    }
}