package  com.example.myapplication.ui.custom.passwordView.pin

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemPinBinding
import com.example.myapplication.databinding.ItemPinTextBinding

class KeyboardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = listOf<Keyboard>()
    private var isTypeImage = true
    private var numberColor = Color.GRAY
    var onClickCallback: ((Keyboard) -> Unit)? = null

    fun setData(data: List<Keyboard>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class TypeImageViewHolder(var binding: ItemPinBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TypeTextViewHolder(var binding: ItemPinTextBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_IMAGE) {
            return TypeImageViewHolder(
                ItemPinBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return TypeTextViewHolder(
                ItemPinTextBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TypeImageViewHolder) {
            try {
                holder.binding.image.setImageDrawable(
                    Drawable.createFromStream(
                        holder.itemView.context.assets.open(
                            data[position].resPath
                        ), null
                    )
                )
            } catch (e: Exception) {

            }
            holder.binding.root.setOnClickListener {
                onClickCallback?.invoke(data[position])
            }
        } else if (holder is TypeTextViewHolder) {
            data.getOrNull(position)?.name?.let {
                if (it == PinLockView.KEY_DELETE) {
                    holder.binding.imgImage.setImageResource(R.drawable.ic_number_delete)
                } else if (it == PinLockView.KEY_CLEAR) {
                    holder.binding.imgImage.setImageResource(R.drawable.ic_number_refresh)
                } else {
                    holder.binding.tvNumber.text = it
                }
                holder.binding.cardImage.setCardBackgroundColor(numberColor)
            }

            holder.binding.root.setOnClickListener {
                onClickCallback?.invoke(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isTypeImage) {
            TYPE_IMAGE
        } else TYPE_TEXT
    }

    fun setPreviewTypeText(numberColor: Int) {
        isTypeImage = false
        this.numberColor = numberColor
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_IMAGE = 1
        const val TYPE_TEXT = 2
    }
}