import java.net.InetAddress

data class PacketData(
    val address: InetAddress,
    val data: ByteArray? = null
)