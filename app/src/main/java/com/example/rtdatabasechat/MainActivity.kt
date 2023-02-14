package com.example.rtdatabasechat

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {


    val TAG = MainActivity::class.java.name

    private var metText: EditText? = null
    private var mbtSent: ImageView? = null
    private var mFirebaseRef: DatabaseReference? = null
//    private var toolbar : Toolbar? = findViewById(R.id.toolbar)

    private var mChats: List<Chat>? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: ChatAdapter? = null
    private var mId: String? = null
    private var settings: ImageView? = null

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkTheme()

//        setSupportActionBar(toolbar)
//        toolbar = findViewById(R.id.toolbar)

        settings = findViewById<ImageView>(R.id.settings)
        metText = findViewById<View>(R.id.message) as EditText
        mbtSent = findViewById<View>(R.id.sent) as ImageView
        mRecyclerView = findViewById<View>(R.id.chat) as RecyclerView
        mChats = ArrayList()

        settings!!.setOnClickListener {
            chooseThemeDialog()
        }



        mId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)


        mAdapter = ChatAdapter(mChats as ArrayList<Chat>,mId as String)
        mRecyclerView!!.adapter = mAdapter


        val database = FirebaseDatabase.getInstance()
        mFirebaseRef = database.getReference("message")


        mbtSent!!.setOnClickListener {
            val message = metText!!.text.toString()
            Log.e("message1",message)
            if (message.isNotEmpty()) {

//                mFirebaseRef!!.push().setValue(Chat(message, mId))
    
                val MESSAGE_CHILD = ""
                mFirebaseRef!!.child(MESSAGE_CHILD).push().setValue(Chat(message,mId))
            }
            metText!!.setText("")
            Log.e("message2", metText.toString())

        }


        mFirebaseRef!!.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                if (dataSnapshot.value != null) {
                    try {
                        val model = dataSnapshot.getValue(Chat::class.java)
                        (mChats as ArrayList<Chat>).add(model!!)
                        mRecyclerView!!.scrollToPosition((mChats as ArrayList<Chat>).size - 1)
                        mAdapter!!.notifyItemInserted((mChats as ArrayList<Chat>).size - 1)
                    } catch (ex: Exception) {
                        Log.e(MainActivity::TAG.toString(), ex.message!!)
                    }
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }
            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(MainActivity::TAG.toString(), databaseError.message)
            }
        })
    }

    private fun chooseThemeDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.choose_theme_text))
        val styles = arrayOf("Light","Dark","System Default")
        val checkedItem = MyPreferences(this).darkMode

        builder.setSingleChoiceItems(styles,checkedItem){ dialog, which ->
            when (which){
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    MyPreferences(this).darkMode = 0
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    MyPreferences(this).darkMode = 1
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    MyPreferences(this).darkMode = 2
                    delegate.applyDayNight()
                    dialog.dismiss()
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkTheme() {
        when (MyPreferences(this).darkMode) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            }
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                delegate.applyDayNight()
            }
        }
    }
}

class MyPreferences(context: Context?) {

    companion object {
        private const val DARK_STATUS = "io.github.manuelernesto.DARK_STATUS"
    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    var darkMode = preferences.getInt(DARK_STATUS, 0)
        set(value) = preferences.edit().putInt(DARK_STATUS, value).apply()

}
