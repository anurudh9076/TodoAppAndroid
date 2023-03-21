package com.example.todoapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.models.Category



class RecyclerCategoryAdapter(val context: Context, var arrayList: ArrayList<Pair<Category,Boolean>>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private  val TAG = "MyTag"
    private var onItemClickListener: OnItemClickListener? = null
    fun set(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    init{
        Log.e(TAG, "init category adapter ", )
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.e(TAG, "onCreateViewHolder: category", )
        val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view, onItemClickListener)

    }

    override fun getItemCount(): Int {
        Log.e(TAG, "getItemCount: ${arrayList.size}", )
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val taskHolder = holder as CategoryViewHolder
        Log.e(TAG, "onBindViewHolder: ", )
        taskHolder.bind(arrayList[position].first,arrayList[position].second)

    }

    class CategoryViewHolder(itemView: View,  val onItemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        private var imageViewCategoryIcon: ImageView
        private var textViewCategoryName: TextView
        private var textViewCategoryDescription: TextView
        private var checkBoxSelectCategory:CheckBox

        private var categoryRow: ConstraintLayout

        init {

            imageViewCategoryIcon = itemView.findViewById(R.id.iv_item_category)
            textViewCategoryName = itemView.findViewById(R.id.tv_item_category_name)
            textViewCategoryDescription = itemView.findViewById(R.id.tv_item_category_description)
            checkBoxSelectCategory=itemView.findViewById(R.id.cb_item_category)
            categoryRow = itemView.findViewById(R.id.category_row)

        }

        fun bind(category: Category,isChecked:Boolean) {
            textViewCategoryName.text = category.name
            textViewCategoryDescription.text = category.description
            if(isChecked)
                checkBoxSelectCategory.isChecked=true


            if (category.iconBitmap != null)
                imageViewCategoryIcon.setImageBitmap(category.iconBitmap)
            else
                imageViewCategoryIcon.setImageResource(R.drawable.icon_category_default)

            checkBoxSelectCategory.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked)
                {
                    if(adapterPosition>=0)
                        onItemClickListener?.onItemChecked(category,adapterPosition)
                }
                else
                {
                    if(adapterPosition>=0)
                        onItemClickListener?.onItemUnchecked(category,adapterPosition)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemChecked(category: Category, position: Int)
        fun onItemUnchecked(category: Category, position: Int)

    }
}