package com.example.todoapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.R
import com.example.todoapp.models.Task

class RecyclerTaskAdapter(val context: Context, public  val arrayList: ArrayList<Task>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener:OnItemClickListener?= null

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
       val taskHolder=holder as TaskViewHolder
        taskHolder.bind(arrayList[position])

    }

    class TaskViewHolder(itemView: View, val onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        var imageViewTaskImage: ImageView
        var textViewTaskName: TextView
        var textViewTaskDescription: TextView
        var textViewTaskCategory: TextView
        var taskRow:ConstraintLayout

        init {
            imageViewTaskImage = itemView.findViewById(R.id.iv_task_row)
            textViewTaskName = itemView.findViewById(R.id.tv_task_name_row)
            textViewTaskDescription = itemView.findViewById(R.id.tv_task_desc_row)
            textViewTaskCategory = itemView.findViewById(R.id.tv_task_category_row)
            taskRow=itemView.findViewById(R.id.task_row)



        }

        fun bind(task:Task) {
            textViewTaskName.text = task.title
            textViewTaskDescription.text = task.description
            textViewTaskCategory.text=task.title

            if(task.imageBitmap!=null)
                imageViewTaskImage.setImageBitmap(task.imageBitmap)

            taskRow.setOnClickListener{
                if (adapterPosition > 0) {
                    onItemClickListener?.onItemClick(task)
                }
//                Log.e("MyTag", "Clicked on task ")
            }

        }


    }
    interface OnItemClickListener {
        fun onItemClick(task: Task)
    }
}