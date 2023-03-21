package com.example.todoapp.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.constants.Constants
import com.example.todoapp.models.Task

class RecyclerTaskAdapter(val context: Context, var arrayList: ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var onItemClickListener: OnItemClickListener? = null
    fun set(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.task_row, parent, false)
        return TaskViewHolder(view, onItemClickListener)

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //  holder.imgContact.setImageResource(arrContacts.get(position).img);
        val taskHolder = holder as TaskViewHolder
        taskHolder.bind(arrayList[position])

    }

    class TaskViewHolder(itemView: View, val onItemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        private var imageViewTaskImage: ImageView
        private var textViewTaskName: TextView
        private var textViewTaskDescription: TextView
        private var textViewTaskCategory: TextView
        private var buttonDeleteTask:ImageView
        private var taskRow: ConstraintLayout

        init {
            imageViewTaskImage = itemView.findViewById(R.id.iv_task_row)
            textViewTaskName = itemView.findViewById(R.id.tv_task_name_row)
            textViewTaskDescription = itemView.findViewById(R.id.tv_task_desc_row)
            textViewTaskCategory = itemView.findViewById(R.id.tv_task_category_row)
            buttonDeleteTask=itemView.findViewById(R.id.btn_delete_task)
            taskRow = itemView.findViewById(R.id.task_row)

        }

        fun bind(task: Task) {
            textViewTaskName.text = task.title
            textViewTaskDescription.text = task.description
            textViewTaskCategory.text = task.status.value

            when(task.status)
            {
                Constants.Status.NOT_STARTED ->{textViewTaskCategory.setTextColor(Color.GRAY)}
                Constants.Status.STARTED -> textViewTaskCategory.setTextColor(Color.BLUE)
                Constants.Status.COMPLETED -> textViewTaskCategory.setTextColor(Color.GREEN)
                Constants.Status.CANCELLED -> textViewTaskCategory.setTextColor(Color.RED)
            }



            if (task.imageBitmap != null)
                imageViewTaskImage.setImageBitmap(task.imageBitmap)
            else
                imageViewTaskImage.setImageResource(R.drawable.icon_task_default1)

            taskRow.setOnClickListener {
                if (adapterPosition >=0) {
                    onItemClickListener?.onItemClick(task,adapterPosition)

                }
            }

            taskRow.setOnLongClickListener {

                if (adapterPosition >= 0) {
                    onItemClickListener?.onItemLongCLick(task)
                }
                true
            }
            buttonDeleteTask.setOnClickListener {
                if (adapterPosition >=0) {
                    onItemClickListener?.onClickButtonDelete(task,adapterPosition)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task,position: Int)
        fun onItemLongCLick(task: Task)

        fun onClickButtonDelete(task:Task,position: Int)
    }
}