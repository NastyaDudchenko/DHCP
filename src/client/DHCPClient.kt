package client

import DHCPController
import DHCPMessage
import DHCPMessage.BOOT_REQUEST
import DHCPMessage.HARDWARE_TYPE
import DHCPMessage.SERVER_PORT
import PacketData
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

fun main() {
    val client: DHCPClient by lazy { DHCPClient() }
    client.run()
}

class DHCPClient : DHCPController() {

    override fun run() {
        println("Opening UDP Socket On Port: ${DHCPMessage.CLIENT_PORT}")

        val serverAddress = InetAddress.getByName("localhost")
        val clientSocket = DatagramSocket(DHCPMessage.CLIENT_PORT)
        var isClientListening = true

        sendPacket(DHCPMessage.Type.DISCOVER, clientSocket, serverAddress)
        while (isClientListening) {
            val remotePacket = catchPacket(clientSocket)
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
                        socket = clientSocket,
                        ipAddress = remotePacket.address
                    )
                }
            } else {
                println("\nNo data from server!")
                isClientListening = false
            }
        }
        clientSocket.close()
    }

    override fun catchPacket(socket: DatagramSocket): PacketData {
        val payload = ByteArray(DHCPMessage.MAX_BUFFER_SIZE)
        val packet = DatagramPacket(payload, payload.size)

        socket.receive(packet)

        println("\nServer>>Connection established from ${packet.address}")
        println("Server>>Data Received: ${packet.data.contentToString()}")
        val packetData = PacketData(
            address = packet.address,
            data = packet.data
        )

        getNtp(packetData)
        getVendorSpecInfo(packetData)
        return packetData
    }

    override fun createPacket(type: DHCPMessage.Type): ByteArray {
        val packetSettings =
            byteArrayOf(BOOT_REQUEST, HARDWARE_TYPE, 6, 0) // OpCode, Hardware Type, Hardware Address Length, Hops
        val options = byteArrayOf(
            53, 1, type.byte, // Option 53: DHCP Message Type (DHCP Offer)
            54, 4, 0, 0, 0, 0, // Option 54: DHCP Server Identifier
            50, 4, 0, 0, 0, 0//Option 50: Requested IP Address
        )

        return packetSettings + options
    }

    override fun sendPacket(type: DHCPMessage.Type, socket: DatagramSocket, ipAddress: InetAddress): ByteArray {
        val rawPacket = createPacket(type)
        val packet = DatagramPacket(rawPacket, rawPacket.size, ipAddress, SERVER_PORT)

        println("\nConnection established from ${packet.address}")
        println("Data Sent: ${packet.data.contentToString()}")
        socket.send(packet)
        return packet.data
    }

    override fun sendPacketByType(type: Int, socket: DatagramSocket, ipAddress: InetAddress) {
        when (type) {
            2 -> sendPacket(type = DHCPMessage.Type.REQUEST, socket = socket, ipAddress = ipAddress)
            5 -> println("Success")
            else -> println("Unknown DHCP Message Type $type")
        }
    }

    private fun getNtp(packetData: PacketData) {
        var optionIndex = -1
        if (packetData.data != null) {
            for (item in packetData.data.withIndex()) {
                if (item.value == DHCPMessage.Option.NTP_SERVER.byte) {
                    optionIndex = item.index
                    break
                }
            }
            if (optionIndex != -1) {
                val list = ArrayList<Byte>()
                val lengthIndex = optionIndex + 1
                val startIndex = lengthIndex + 1
                val endIndex = startIndex + packetData.data[lengthIndex] - 1
                for (item in startIndex..endIndex) {
                    list.add(packetData.data[item])
                }
                println("NTP: $list")
            }
        }
    }

    private fun getVendorSpecInfo(packetData: PacketData) {
        var optionIndex = -1
        if (packetData.data != null) {
            for (item in packetData.data.withIndex()) {
                if (item.value == DHCPMessage.Option.VENDOR_SPECIFIC_INFO.byte) {
                    optionIndex = item.index
                    break
                }
            }
            if (optionIndex != -1) {
                val list = ArrayList<Byte>()
                val lengthIndex = optionIndex + 1
                val startIndex = lengthIndex + 1
                val endIndex = startIndex + packetData.data[lengthIndex] - 1
                for (item in startIndex..endIndex) {
                    list.add(packetData.data[item])
                }
                println("Vendor: ${String(list.toByteArray(), Charsets.UTF_8)}")
            }
        }
    }
}