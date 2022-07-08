NETWORK_NAME="${PWD##*/}_default"

if [ -n "$EVENTUATE_PROJECT_NAME" ] ; then
    NETWORK_NAME="${EVENTUATE_PROJECT_NAME}_default"
fi
