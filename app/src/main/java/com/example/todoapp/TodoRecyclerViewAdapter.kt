package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.TodoData

class TodoRecyclerViewAdapter(private val clickListener: (TodoData) -> Unit) :
    RecyclerView.Adapter<TodoViewHolder>() {
    private val todoList = ArrayList<TodoData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val todoItem = layoutInflater.inflate(R.layout.task_item, parent, false)
        return TodoViewHolder(todoItem)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        return holder.bind(todoList[position], clickListener)
    }

    fun setTodo(todo: List<TodoData>) {
        todoList.clear()
        todoList.addAll(todo)
    }
}


class TodoViewHolder(private val view: View) : ViewHolder(view) {
    fun bind(data: TodoData, clickListener: (TodoData) -> Unit) {
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val desc = view.findViewById<TextView>(R.id.tvDescription)
        val priority = view.findViewById<View>(R.id.priority_indicator)

        title.text = data.title
        desc.text = data.description
        val priorityColor = when (data.priority) {
            Priority.High -> android.graphics.Color.RED
            Priority.Low -> android.graphics.Color.GREEN
            Priority.Medium -> android.graphics.Color.BLUE
        }

        priority.setBackgroundColor(priorityColor)
        view.setOnClickListener {
            clickListener(data)
        }
    }


}