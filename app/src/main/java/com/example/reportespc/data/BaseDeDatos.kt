package com.example.reportespc.data
import android.content.Context
import androidx.room.*
import com.example.reportespc.data.entity.Reporte
import com.example.reportespc.data.repo.ReporteDao
import kotlinx.coroutines.flow.Flow

//La Base del dto
@Database(entities = [Reporte::class], version = 1, exportSchema = false)
abstract class ReporteDatabase : RoomDatabase() {
    abstract fun reporteDao(): ReporteDao

    companion object {
        @Volatile
        private var INSTANCE: ReporteDatabase? = null

        fun getDatabase(context: Context): ReporteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReporteDatabase::class.java,
                    "reportes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}