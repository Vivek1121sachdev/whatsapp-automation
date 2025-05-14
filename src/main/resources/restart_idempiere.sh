#!/bin/bash

# Validate input
if [ "$#" -ne 3 ]; then
    echo "Usage: sudo $0 <ssh_key.pem> <username> <ip_address>"
    exit 1
fi

KEY_FILE=$1
USERNAME=$2
SERVER_IP=$3

# Validate SSH key file
if [ ! -f "$KEY_FILE" ]; then
    echo "Error: SSH key file '$KEY_FILE' not found."
    exit 2
fi

echo "Connecting to $USERNAME@$SERVER_IP with key $KEY_FILE..."

ssh -i "$KEY_FILE" "$USERNAME@$SERVER_IP" << EOF
echo "Stopping idempiere service (1st attempt)..."
sudo service idempiere stop

echo "Stopping idempiere service (2nd attempt)..."
sudo service idempiere stop

echo "Checking idempiere service status..."
status=\$(sudo service idempiere status | grep -i "Active:")

echo "\$status"

if echo "\$status" | grep -q "active (running)"; then
    echo "Service is still active. Attempting to locate and kill process..."

    pids=\$(ps -aux | grep java | grep idempiere | grep -v grep | awk '{print \$2}')

    if [ -n "\$pids" ]; then
        echo "Found running idempiere process IDs: \$pids"
        for pid in \$pids; do
            echo "Killing process ID \$pid"
            sudo kill -9 "\$pid"
        done
    else
        echo "No running idempiere process found."
    fi
else
    echo "Service is not running."
    sudo service idempiere start
    sudo service idempiere status | grep -i "Active: active"
fi
EOF