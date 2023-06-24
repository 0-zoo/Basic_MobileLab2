package com.example.demo01


import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import java.text.SimpleDateFormat
import java.util.*

class DataDao {
    private var databaseReference: DatabaseReference? = null
    val db = FirebaseDatabase.getInstance()
    val calendar = Calendar.getInstance()

    fun getDayOfWeek(): String?{
        val nWeek = calendar.get(Calendar.DAY_OF_WEEK)
        var strWeek: String? = null
        if(nWeek == 1){
            strWeek = "일"
        }else if(nWeek == 2){
            strWeek = "월"
        }else if(nWeek == 3){
            strWeek = "화"
        }else if(nWeek == 4){
            strWeek = "수"
        }else if(nWeek == 5){
            strWeek = "목"
        }else if(nWeek == 6){
            strWeek = "금"
        }else if(nWeek == 7){
            strWeek = "토"
        }
        return strWeek
    }
    fun add(data : Data?,User : String): Task<Void> {
        databaseReference = db.getReference(User).child(getDayOfWeek().toString())
        return databaseReference!!.setValue(data)
    }
    fun getData(): Query?{
        databaseReference = db.getReference()
        return databaseReference
    }
}