package com.cashfulus.cashcarplus.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.databinding.library.baseAdapters.BR

abstract class BaseFragment<B: ViewDataBinding, VM: ViewModel>(@LayoutRes val layoutId: Int) : Fragment() {
    protected abstract val viewModel: VM
    protected lateinit var binding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    /**
     * Get layout resource id which inflate in onCreateView.
     */
    /*@LayoutRes
    abstract fun getLayoutResId(): Int*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doDataBinding()
    }

    /**
     * Do your other stuff in init after binding layout.
     */
    abstract fun init()

    private fun doDataBinding() {
        binding.lifecycleOwner = viewLifecycleOwner // it is extra if you want to set life cycle owner in binding
        // Here your viewModel and binding variable imlementation
        binding.setVariable(BR.viewModel, viewModel)  // In all layout the variable name should be "viewModel"
        binding.executePendingBindings()
        init()
    }

    fun showToast(msg: String) = Toast.makeText(requireActivity(), msg, Toast.LENGTH_LONG).show()
    fun showToastShort(msg: String) = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
}