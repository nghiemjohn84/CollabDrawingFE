package com.example.collabdrawingfe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem

class PaintActivity : AppCompatActivity() {

    private var paintView: PaintView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        paintView = findViewById(R.id.paintView) as PaintView
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        paintView!!.init(metrics)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.canvas, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                paintView!!.clear()
                return true
            }
            R.id.eraser -> {
                paintView!!.eraser()
                return true
            }
            R.id.brush -> {
                paintView!!.brush()
                return true
            }
            R.id.red ->{
                paintView!!.red()
                return true
            }
            R.id.black ->{
                paintView!!.black()
                return true
            }
            R.id.blue ->{
                paintView!!.blue()
                return true
            }
            R.id.green ->{
                paintView!!.green()
                return true
            }
            R.id.white ->{
                paintView!!.white()
                return true
            }
            R.id.invite -> {
                val activityIntent = Intent(this, InviteCollaboratorActivity::class.java)
                startActivity(activityIntent)
            }

            R.id.gallery -> {
                val activityIntent = Intent(this, GalleryActivity::class.java)
                startActivity(activityIntent)

            }
            R.id.save -> {
                val activityIntent = Intent(this, GalleryActivity::class.java)
                //call itemtobesaved method here?
                startActivity(activityIntent)

            }

        }

        return super.onOptionsItemSelected(item)
    }
}