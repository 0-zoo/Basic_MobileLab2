package com.example.demo01

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        val dao = DataDao()
        val bpm_mon = findViewById<TextView>(R.id.bpm_mon)
        val bpm_tue = findViewById<TextView>(R.id.bpm_tue)
        val bpm_wed = findViewById<TextView>(R.id.bpm_wed)
        val bpm_thu = findViewById<TextView>(R.id.bpm_thu)
        val bpm_fri = findViewById<TextView>(R.id.bpm_fri)
        val bpm_sat = findViewById<TextView>(R.id.bpm_sat)
        val bpm_sun = findViewById<TextView>(R.id.bpm_sun)
        val str_mon = findViewById<TextView>(R.id.str_mon)
        val str_tue = findViewById<TextView>(R.id.str_tue)
        val str_wed = findViewById<TextView>(R.id.str_wed)
        val str_thu = findViewById<TextView>(R.id.str_thu)
        val str_fri = findViewById<TextView>(R.id.str_fri)
        val str_sat = findViewById<TextView>(R.id.str_sat)
        val str_sun = findViewById<TextView>(R.id.str_sun)
        val btn_mon = findViewById<Button>(R.id.graph_mon)
        val btn_tue = findViewById<Button>(R.id.graph_tue)
        val btn_wed = findViewById<Button>(R.id.graph_wed)
        val btn_thu = findViewById<Button>(R.id.graph_thu)
        val btn_fri = findViewById<Button>(R.id.graph_fri)
        val btn_sat = findViewById<Button>(R.id.graph_sat)
        val btn_sun = findViewById<Button>(R.id.graph_sun)
        val avg_bpm = findViewById<TextView>(R.id.avg_record)
        var chart : String? = null
        val id = intent.getStringExtra("Email")
        dao.getData()?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var avg : Long = 0
                var a : Int = 0
                for (dataSnapShot in snapshot.children){
                    if(dataSnapShot.exists() && dataSnapShot.key.toString() == id.toString()){
                        val user = dataSnapShot.getValue() as? HashMap<String,Any>
                        for(i in 1..7){
                            if(i == 1){
                                val data = user?.get("일") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_sun.text = "-"
                                    str_sun.text = "-"
                                }else{
                                    bpm_sun.text = "${data?.get("bpm")}"
                                    str_sun.text = "${data?.get("str")}"
                                    avg = avg + data?.get("bpm") as Long
                                    chart = data?.get("chart") as String?
                                    a += 1
                                }
                            }else if(i == 2){
                                val data = user?.get("월") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_mon.text = "-"
                                    str_mon.text = "-"
                                }else{
                                    bpm_sun.text = "${data?.get("bpm")}"
                                    str_sun.text = "${data?.get("str")}"
                                    chart = data?.get("chart") as String?
                                    avg = avg + data?.get("bpm") as Long
                                    a+=1
                                }
                            }else if(i == 3){
                                val data = user?.get("화") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_tue.text = "-"
                                    str_tue.text = "-"
                                }else{
                                    bpm_tue.text = "${data?.get("bpm")}"
                                    str_tue.text = "${data?.get("str")}"
                                    chart = data?.get("chart") as String?
                                    avg = avg + data?.get("bpm") as Long
                                    a+=1
                                }
                            }else if(i == 4){
                                val data = user?.get("수") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_wed.text = "-"
                                    str_wed.text = "-"
                                }else{
                                    bpm_wed.text = "${data?.get("bpm")}"
                                    str_wed.text = "${data?.get("str")}"
                                    chart = data?.get("chart") as String?
                                    avg = avg + data?.get("bpm") as Long
                                    a+=1
                                }
                            }else if(i == 5){
                                val data = user?.get("목") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_thu.text = "-"
                                    str_thu.text = "-"
                                }else{
                                    bpm_thu.text = "${data?.get("bpm")}"
                                    str_thu.text = "${data?.get("str")}"
                                    chart = data?.get("chart") as String?
                                    avg = avg + data?.get("bpm") as Long
                                    a+=1
                                }
                            }else if(i == 6){
                                val data = user?.get("금") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_fri.text = "-"
                                    str_fri.text = "-"
                                }else{
                                    bpm_fri.text = "${data?.get("bpm")}"
                                    str_fri.text = "${data?.get("str")}"
                                    chart = data?.get("chart") as String?
                                    avg = avg + data?.get("bpm") as Long
                                    a+=1
                                }
                            }else if(i == 7){
                                val data = user?.get("토") as? HashMap<String,Any>
                                if(data?.get("bpm")== null){
                                    bpm_sat.text = "-"
                                    str_sat.text = "-"
                                }else{
                                    bpm_sat.text = "${data?.get("bpm")}"
                                    str_sat.text = "${data?.get("str")}"
                                    chart = data?.get("chart") as String?
                                    avg = avg + data?.get("bpm") as Long
                                    a+=1
                                }
                            }
                        }
                    }


                }
                avg_bpm.text = "평균 BPM : ${avg/a}"
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        btn_mon.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_mon.text)
            intent.putExtra("str",str_mon.text)
            startActivity(intent)
            finish()
        }
        btn_tue.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_tue.text)
            intent.putExtra("str",str_mon.text)
            startActivity(intent)
            finish()
        }
        btn_wed.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_wed.text)
            intent.putExtra("str",str_wed.text)
            startActivity(intent)
            finish()
        }
        btn_thu.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_thu.text)
            intent.putExtra("str",str_thu.text)
            startActivity(intent)
            finish()
        }
        btn_fri.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_fri.text)
            intent.putExtra("str",str_fri.text)
            startActivity(intent)
            finish()
        }
        btn_sat.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_sat.text)
            intent.putExtra("str",str_sat.text)
            startActivity(intent)
            finish()
        }
        btn_sun.setOnClickListener {
            val intent = Intent(this,RecordGraphActivity::class.java)
            intent.putExtra("graph",chart)
            intent.putExtra("bpm",bpm_sun.text)
            intent.putExtra("str",str_sun.text)
            startActivity(intent)
            finish()
        }
    }
}