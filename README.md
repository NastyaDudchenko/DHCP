# DHCP Client-Server Project

## Overview

This project is a simple implementation of a DHCP Client and DHCP Server written in Kotlin. The DHCP (Dynamic Host Configuration Protocol) protocol is used to dynamically assign IP addresses and provide configuration information to clients in a network. This project simulates both a DHCP server and a client that communicates over UDP to exchange DHCP messages.

The DHCP server listens for incoming DHCP requests and sends back appropriate responses (e.g., OFFER, ACK). The DHCP client sends a DISCOVER message to the server and processes the server's response.

### Key Features:
- The DHCP Server listens on a specified port and responds to DHCP Discover requests.
- The DHCP Client sends a DHCP Discover message to find the server and then processes the response.
- Handles DHCP options such as NTP Server and Vendor-Specific Information.
- Communication occurs via UDP packets.

## Requirements

- Kotlin 1.5+.
- Java runtime (JRE 8+).

## Setup

1. Clone the repository:

    ```bash
    git clone https://github.com/yourusername/dhcp-client-server.git
    cd dhcp-client-server
    ```

2. Build and run the project:
   - Use IntelliJ IDEA or any Kotlin IDE.
   - Run `DHCPServer.kt` for the server.
   - Run `DHCPClient.kt` for the client.

## How It Works

1. **Client**: The client sends a DHCP Discover message to the server. It listens for a response, processes the server's offer, and requests an IP address.
2. **Server**: The server listens for client requests and sends back a DHCP Offer or Acknowledgment message with the configuration details.

### Communication:
- **Discover**: Client requests configuration.
- **Offer**: Server offers configuration (IP address).
- **Request**: Client requests the offered configuration.
- **ACK**: Server acknowledges the request.

## Example Output

### Server:
```bash
Server>>Received data from client: [Discover message]
Server>>Sending Offer with IP: 192.168.1.10
```

### Client:
```bash
Client>>Received Offer from server: [IP: 192.168.1.10]
Client>>NTP Server: [192.168.1.1]
```

## License
This project is open-source. Feel free to modify and distribute it as needed.

