package pl.konarzewski.uboze.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import pl.konarzewski.uboze.database.AppDatabase
import pl.konarzewski.uboze.database.dao.ImigeDao
import pl.konarzewski.uboze.engine.Engine

class MainViewModel(ctx: Context) : ViewModel() {

    private val db: AppDatabase = AppDatabase.getInstance(ctx)
    private val imigeDao: ImigeDao = db.imigeDao()
    private var engine: Engine
    private val paths: List<String>

    init {
        engine = Engine(ctx)
        paths = engine.getPaths()
    }

    fun getPaths(): List<String> {
        return paths
    }
}