package app.example.rates.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import app.example.rates.BR


abstract class BindingRecyclerAdapter<T, H> internal constructor(
    private val handler: H,
    private val viewModel: ViewModel
) :
    RecyclerView.Adapter<BindingRecyclerAdapter<T, H>.GenericViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GenericViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater, viewType, parent, false
        )
        return GenericViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: GenericViewHolder,
        position: Int
    ) {
        val item = getItemForPosition(position)
        holder.bind(item, handler, viewModel)
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    protected abstract fun getItemForPosition(position: Int): T

    protected abstract fun getLayoutIdForPosition(position: Int): Int

    inner class GenericViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: T, handler: H, viewModel: ViewModel) {
            binding.setVariable(BR.item, item)
            binding.setVariable(BR.clickHandler, handler)
            binding.setVariable(BR.viewModel, viewModel)
            binding.executePendingBindings()
        }
    }
}

interface BindableAdapter<T> {
    fun setViewState(viewState: T)
}
