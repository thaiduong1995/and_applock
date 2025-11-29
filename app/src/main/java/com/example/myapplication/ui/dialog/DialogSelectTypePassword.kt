package com.example.myapplication.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.base.BaseDialog
import com.example.myapplication.data.model.Password
import com.example.myapplication.databinding.DialogSelectTypePasswordBinding
import com.example.myapplication.extention.getArgumentParcelableArray
import com.example.myapplication.ui.adapter.TypePasswordAdapter

class DialogSelectTypePassword : BaseDialog<DialogSelectTypePasswordBinding>() {

    private var adapter: TypePasswordAdapter? = null
    var callback: (Password) -> Unit = {}

    override fun createView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSelectTypePasswordBinding {
        return DialogSelectTypePasswordBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
        context?.let { context ->
            arguments?.getArgumentParcelableArray<Password>(LIST_PASSWORD_TYPES)?.let { list ->
                adapter = TypePasswordAdapter(ArrayList(list)) {
                    callback.invoke(it)
                    dismiss()
                }
            }
            binding.recycler.adapter = adapter
            binding.recycler.layoutManager = LinearLayoutManager(context)
        }
    }

    companion object {

        private const val LIST_PASSWORD_TYPES = "LIST_PASSWORD_TYPES"

        @JvmStatic
        fun newInstance(
            listPasswordTypes: Array<Password> = arrayOf()
        ) = DialogSelectTypePassword().apply {
                arguments = bundleOf(LIST_PASSWORD_TYPES to listPasswordTypes)
            }
    }
}