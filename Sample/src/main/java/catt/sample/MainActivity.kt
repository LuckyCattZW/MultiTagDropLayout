package catt.sample

import android.os.Bundle
import android.util.Log.e
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val _TAG:String by lazy { MainActivity::class.java.simpleName }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            e(_TAG, "onClick: button")
        }

        multi_tab_drop_layout.adapter = MultiTabDropAdapter(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        this.clearFindViewByIdCache()
    }
}
