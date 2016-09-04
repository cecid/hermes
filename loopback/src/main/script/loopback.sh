#!/bin/sh

if [ "$1" = "ebms" ]; then
    sh loopback-ebms.sh
elif [ "$1" = "as2" ]; then
    sh loopback-as2.sh
else
    echo "Usage:  sh loopback.sh ( protocols ... )"
    echo "protocols:"
    echo "  ebms              Loop back test for ebMS protocol"
    echo "  as2               Loop back test for AS2 protocol"
fi
