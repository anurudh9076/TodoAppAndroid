package com.example.todoapp.sealedClasses

import com.example.todoapp.models.Category
import com.example.todoapp.models.Task

sealed class CategoryOperation
{


    data class OnSuccessFetchAllCategories(val list: ArrayList<Category>) : CategoryOperation()
    data class OnSuccessUpdateCategory(val category: Category, val position: Int) : CategoryOperation()
    data class OnSuccessDeleteCategory (val category: Category, val position: Int) : CategoryOperation()
    data class OnSuccessAddCategory(val category: Category) : CategoryOperation()


    data class OnErrorFetchAllCategory(val error: String) : CategoryOperation()
    data class OnErrorUpdateCategory(val error: String) : CategoryOperation()
    data class OnErrorDeleteCategory(val error: String) : CategoryOperation()
    data class OnErrorAddCategory(val error: String) : CategoryOperation()

    data class OnNullOperation(val error: String) : CategoryOperation()
}
