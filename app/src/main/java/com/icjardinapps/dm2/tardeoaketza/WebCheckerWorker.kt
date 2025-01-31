package com.icjardinapps.dm2.tardeoaketza
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import org.jsoup.Jsoup
import java.lang.Exception

/**
 * Worker que realiza la tarea de comprobar una página web en segundo plano
 * para verificar si contiene una palabra específica y, si es así, envía
 * una notificación.
 *
 * <p>Esta clase utiliza `Jsoup` para realizar la conexión HTTP y extraer
 * el contenido de texto de una página web. La URL y la palabra a buscar
 * se almacenan en las preferencias compartidas de la aplicación
 * (`SharedPreferences`). Si la palabra se encuentra en el contenido de la
 * página, se envía una notificación al usuario.</p>
 *
 * @param appContext El contexto de la aplicación.
 * @param workerParams Parámetros adicionales proporcionados por WorkManager.
 * @author Aketza
 * @version 1.0
 */
class WebCheckerWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    /**
     * Metodo principal que realiza la tarea en segundo plano.
     *
     * <p>Obtiene la URL y la palabra a buscar desde `SharedPreferences`,
     * se conecta a la página web usando `Jsoup`, y verifica si la palabra
     * está presente en el contenido. Si encuentra la palabra, llama a
     * `sendNotification` para enviar una notificación.</p>
     *
     * @return Un objeto [Result] que indica si el trabajo fue exitoso o no.
     * @author Aketza
     */
    override fun doWork(): Result {
        try {
            val sharedPreferences = applicationContext.getSharedPreferences("WebCheckerPrefs", Context.MODE_PRIVATE)

            val semaforo = sharedPreferences.getString("semaforo", null) ?: return Result.failure()

            if (isStopped || semaforo.equals("R")) {
                return Result.failure()

            }
            // Obtener la URL y la palabra desde las preferencias compartidas
            val url = sharedPreferences.getString("url", null) ?: return Result.failure()
            val word = sharedPreferences.getString("word", null) ?: return Result.failure()
            if (isStopped || semaforo.equals("R")) {
                return Result.failure()
            }
            // Conectar a la página web
            val doc = Jsoup.connect(url).get()
            if (isStopped || semaforo.equals("R")) {
                return Result.failure()
            }

            // Verificar si la palabra está presente
            if (doc.text().contains(word, ignoreCase = true)) {
                sendNotification()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    /**
     * Envía una notificación al usuario si se encuentra la palabra en la página web.
     *
     * <p>La notificación se muestra con el título "¡Se encontró la palabra!"
     * y un mensaje que indica que la palabra fue encontrada.</p>
     *
     * @author Aketza
     */
    private fun sendNotification() {
        val context = applicationContext

        // Crear el canal de notificación (solo necesario para API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                context.getString(R.string.guestlist_channel),
                context.getString(R.string.guestlist_notification),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    context.getString(/* resId = */ R.string.canal_para_notificaciones_de_palabras_encontradas)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Crear y enviar la notificación
        val notification: Notification = NotificationCompat.Builder(context, context.getString(R.string.guestlist_channel))
            .setContentTitle(context.getString(R.string.se_encontr_la_palabra))
            .setContentText(context.getString(R.string.la_palabra_fue_encontrada_en_la_p_gina_web))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}