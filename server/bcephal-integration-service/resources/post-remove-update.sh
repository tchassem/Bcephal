#!/bin/sh
BCEPHAL_PATH="/opt/bcephal/v8/bcephal-integration-service"
RUN_PATH="$BCEPHAL_PATH/runManager.sh"
sudo su <<EOFF
rm -f "RUN_PATH"
rm -rf "$BCEPHAL_PATH/"
EOFF