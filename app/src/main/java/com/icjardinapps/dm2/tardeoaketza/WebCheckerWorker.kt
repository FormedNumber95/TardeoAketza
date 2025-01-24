package com.icjardinapps.dm2.tardeoaketza

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WebCheckerWorker(appContext: Context, workerParams:
WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
       try{
           mediante sharePreferences pasamos la palabra y la url
                   desde MainActivity
// Realizamos el scraping de la página
           val doc = Jsoup.connect(url).get()
// Verificamos si la palabra está en el texto de la
           página
           if (doc.text().contains(palabra, ignoreCase = true)) {
               sendNotification()
           }
       } catch (e: Exception) {
// En caso de error, retornamos un fallo
           return Result.failure()
       }
// Si todo salió bien, retornamos un éxito
        return Result.success()
       }
    }

}