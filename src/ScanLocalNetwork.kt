import java.io.OutputStream
import java.net.Socket

fun main() {
    val serverIp = "192.168.50.255" // Замените на IP-адрес сервера
    val serverPort = 67 // Замените на порт, на котором сервер слушает

    try {
        val socket = Socket(serverIp, serverPort)
        val outputStream: OutputStream = socket.getOutputStream()

        val message = "Привет, сервер!"
        outputStream.write(message.toByteArray())

        outputStream.close()
        socket.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
