package com.vfs.somecoolname

data class Task(val name: String, var completed: Boolean)

data class  Group(val name: String, var tasks: MutableList<Task>)

class AppData {
    companion object {
        var groups: MutableList<Group> = mutableListOf()
        
        fun initialize(){
            val task1 = Task("Buy groceries", false)
            val task2 = Task("Do laundry", false)
            val task3 = Task("Clean the house", false)
            val task4 = Task("Go for a walk", false)
            val task5 = Task("Call mom", false)
            val task6 = Task("Pay bills", false)
            val task7 = Task("Go to the gym", false)
            val task8 = Task("Read a book", false)
            val task9 = Task("Watch a movie", false)
            val task10 = Task("Go to the park", false)
            val task11 = Task("Go to the store", false)
            val task12 = Task("Go to the doctor", false)
            val task13 = Task("Go to the dentist", false)
            val task14 = Task("Go to the pharmacy", false)
            val task15 = Task("Go to the gas station", false)
            
            val group1 = Group("Party on Campus", mutableListOf(task8, task3, task4, task5))
            val group2 = Group("Work", mutableListOf(task6, task7, task9, task10))
            val group3 = Group("School", mutableListOf(task11, task12, task13, task14, task15))
            val group4 = Group("Family", mutableListOf(task1, task2, task4))
            val group5 = Group("Friends", mutableListOf(task7, task9, task10, task11, task12, task13, task14, task15))
            
            groups = mutableListOf(group1, group2, group3, group4, group5)
        }
    }
}