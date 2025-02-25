object DHCPMessage {
    const val MAX_BUFFER_SIZE = 1024
    const val SERVER_PORT = 67
    const val CLIENT_PORT = 68

    /**
     * General type message
     * 1=request | 2= reply
     * No other values possible
     */
    const val BOOT_REQUEST: Byte = 1
    const val BOOT_REPLY: Byte = 2

    /**
     * Hardware type
     * 1 = 10MB ethernet
     * 6 = IEE802 Network
     * 7 = ARCNET
     * 11 = Localtalk
     * 12 = Localnet
     * 14 = SMDS
     * 15 = Frame relay
     * 16 = Asynchronous Transfer mode
     * 17 = HDLC
     * 18 = Fibre channel
     * 19 = Asynchronous Transfer mode
     * 20 = Serial Line
     */
    const val HARDWARE_TYPE: Byte = 1

    enum class Option(val byte: Byte) {
        SUBNET_MASK(1.toByte()),
        ROUTER(3.toByte()),
        DNS_SERVER(6.toByte()),
        NTP_SERVER(42.toByte()),
        VENDOR_SPECIFIC_INFO(43.toByte()),
        IP_ADDRESS_LEASE_TIME(51.toByte()),
        MESSAGE_TYPE(53.toByte()),
        SERVER_IDENTIFIER(54.toByte()),
        ERROR_MESSAGE(56.toByte())
    }

    enum class Type(val byte: Byte) {
        DISCOVER(1.toByte()),
        OFFER(2.toByte()),
        REQUEST(3.toByte()),
        DECLINE(4.toByte()),
        ACK(5.toByte()),
        NAK(6.toByte()),
        RELEASE(7.toByte()),
        INFO(8.toByte())
    }
}