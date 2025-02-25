package server

import DHCPController
import DHCPMessage
import DHCPMessage.BOOT_REPLY
import DHCPMessage.CLIENT_PORT
import DHCPMessage.HARDWARE_TYPE
import PacketData
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun main(args: Array<String>) {
    val server: DHCPController by lazy { DHCPServer() }
    server.run()
}

class DHCPServer : DHCPController() {

    override fun run() {
        println("Opening UDP Socket On Port: ${DHCPMessage.SERVER_PORT}")

        val serverSocket = DatagramSocket(DHCPMessage.SERVER_PORT)
        var isServerListening = true

        while (isServerListening) {
            val remotePacket = catchPacket(serverSocket)
            var optionIndex = -1

            if (remotePacket.data != null) {
                for (item in remotePacket.data.withIndex()) {
                    if (item.value == DHCPMessage.Option.MESSAGE_TYPE.byte) {
                        optionIndex = item.index
                        break
                    }
                }

                if (optionIndex != -1) {
                    val messageType = remotePacket.data[optionIndex + 2]
                    sendPacketByType(
                        type = messageType.toInt(),
                        socket = serverSocket,
                        ipAddress = remotePacket.address
                    )
                }
            } else {
                println("\nNo data from client!")
                isServerListening = false
            }
        }
        serverSocket.close()
    }

    override fun sendPacketByType(type: Int, socket: DatagramSocket, ipAddress: InetAddress) {
        when (type) {
            1 -> sendPacket(type = DHCPMessage.Type.OFFER, socket = socket, ipAddress = ipAddress)
            3 -> sendPacket(type = DHCPMessage.Type.ACK, socket = socket, ipAddress = ipAddress)
            else -> println("\nUnknown DHCP Message Type $type")
        }
    }

    override fun catchPacket(socket: DatagramSocket): PacketData {
        val payload = ByteArray(DHCPMessage.MAX_BUFFER_SIZE)
        val packet = DatagramPacket(payload, payload.size)

        socket.receive(packet)

        println("\nClient>>Connection established from ${packet.address}")
        println("Client>>Data Received: ${packet.data.contentToString()}")
        return PacketData(
            address = packet.address,
            data = packet.data
        )
    }

    override fun sendPacket(type: DHCPMessage.Type, socket: DatagramSocket, ipAddress: InetAddress): ByteArray {
        val rawPacket = createPacket(type)
        val packet = DatagramPacket(rawPacket, rawPacket.size, ipAddress, CLIENT_PORT)

        println("\nConnection established from ${packet.address}")
        println("Data Send: ${packet.data.contentToString()}")
        socket.send(packet)
        return packet.data
    }

    override fun createPacket(type: DHCPMessage.Type): ByteArray {
        val packetSettings =
            byteArrayOf(BOOT_REPLY, HARDWARE_TYPE, 6, 0) // OpCode, Hardware Type, Hardware Address Length, Hops
        val site = "https://www.google.com/".toByteArray(Charsets.UTF_8)
        val vendorSpecificInfo = byteArrayOf(43, site.size.toByte()) + site
        val options = byteArrayOf(
            53, 1, type.byte, // Option 53: DHCP Message Type (DHCP Offer)
            54, 4, 192.toByte(), 168.toByte(), 1, 10, // Option 54: DHCP Server Identifier
            51, 4, 0, 0, 1, 2, // Option 51: IP Address Lease Time
            1, 4, 0, 0, 1, 44, // Option 1: Subnet Mask
            3, 4, 192.toByte(), 168.toByte(), 1, 1, // Option 3: Router (Default Gateway)
            6, 4, 192.toByte(), 168.toByte(), 1, 2, // Option 6: DNS Server
            42, 4, 192.toByte(), 168.toByte(), 1, 3 // Option 42: NTP Servers
        ) + vendorSpecificInfo // Option 43: Vendor-Specific Information

        return packetSettings + options
    }
}