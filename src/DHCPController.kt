import java.net.DatagramSocket
import java.net.InetAddress

abstract class DHCPController {
    abstract fun run()

    protected abstract fun catchPacket(socket: DatagramSocket): PacketData

    protected abstract fun sendPacket(type: DHCPMessage.Type, socket: DatagramSocket, ipAddress: InetAddress): ByteArray

    protected abstract fun createPacket(type: DHCPMessage.Type): ByteArray

    protected abstract fun sendPacketByType(type: Int, socket: DatagramSocket, ipAddress: InetAddress)
}