#!/usr/bin/env bash
# ============================================
# wait-for-it.sh - Wait for service readiness
# ============================================
# Usage:
#   ./wait-for-it.sh host:port [-t timeout] [-q] [-- command args]
#
# Examples:
#   ./wait-for-it.sh mysql:3306 -t 60 -- echo "MySQL is ready"
#   ./wait-for-it.sh nacos:8848 -t 120
# ============================================

set -e

TIMEOUT=60
QUIET=0
HOST=""
PORT=""
CMD=""

usage() {
    echo "Usage: $0 host:port [-t timeout] [-q] [-- command args]"
    exit 1
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
        *:* )
            HOST="${1%%:*}"
            PORT="${1##*:}"
            shift 1
            ;;
        -t )
            TIMEOUT="$2"
            shift 2
            ;;
        -q )
            QUIET=1
            shift 1
            ;;
        -- )
            shift
            CMD="$*"
            break
            ;;
        -* )
            usage
            ;;
        * )
            usage
            ;;
    esac
done

if [[ -z "$HOST" ]] || [[ -z "$PORT" ]]; then
    usage
fi

# Log function
log() {
    if [[ $QUIET -eq 0 ]]; then
        echo "[wait-for-it] $*"
    fi
}

# Check if a host:port is reachable
check_port() {
    timeout 1 bash -c "echo > /dev/tcp/$HOST/$PORT" 2>/dev/null
    return $?
}

log "Waiting for $HOST:$PORT (timeout: ${TIMEOUT}s)..."

START_TIME=$(date +%s)
WAITING=true

while $WAITING; do
    if check_port; then
        log "$HOST:$PORT is available!"
        WAITING=false
    else
        CURRENT_TIME=$(date +%s)
        ELAPSED=$((CURRENT_TIME - START_TIME))

        if [[ $ELAPSED -ge $TIMEOUT ]]; then
            log "TIMEOUT: $HOST:$PORT not available after ${TIMEOUT}s"
            exit 1
        fi

        if [[ $QUIET -eq 0 ]] && [[ $((ELAPSED % 5)) -eq 0 ]] && [[ $ELAPSED -gt 0 ]]; then
            log "Still waiting for $HOST:$PORT (${ELAPSED}s elapsed)..."
        fi

        sleep 1
    fi
done

# Run the command if provided
if [[ -n "$CMD" ]]; then
    exec $CMD
fi
