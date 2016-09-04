#!/bin/sh

if [ "$1" = "ebms" ]; then
    if [ "$2" = "-a" -o "$2" == "-d" ]; then
        if [ "$3" != "" ]; then
            if [ -r "$3" ]; then
                sh partnership-ebms.sh "$2" "$3"
                exit 1
            else
              echo "The file '$3' does not exist."
              exit 1
            fi
        fi
    fi
elif [ "$1" = "as2" ]; then
    if [ "$2" = "-a" -o "$2" == "-d" ]; then
        if [ "$3" != "" ]; then
            if [ -r "$3" ]; then
                sh partnership-as2.sh "$2" "$3"
                exit 1
            else
              echo "The file '$3' does not exist."
              exit 1
            fi
        fi
    fi
fi

echo "Usage:  sh partnership.sh ( protocols ... ) ( options... ) 'partnership_xml'"
echo "protocols:"
echo "  ebms              Partnership maintenance for ebMS protocol"
echo "  as2               Partnership maintenance for AS2 protocol"
echo "options:"
echo "  -a                Add partnership"
echo "  -d                Remove partnership"
