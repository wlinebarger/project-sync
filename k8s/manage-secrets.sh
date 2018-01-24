#!/bin/sh

PROJECT=professionalserviceslabs
KEYRING=CDL
KEY=coeur-sync
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
VALUES_DIR="${SCRIPT_DIR}/helm/values"
PLAIN_FILE="${VALUES_DIR}/${PROJECT}/secrets.yaml"
CIPHER_FILE="${VALUES_DIR}/${PROJECT}/secrets.enc"

usage () {
  echo "Error encrypt/decrypt secrets file script call. Usage:"
  echo "`basename "$0"` [-e|-d]"
}

while getopts ":ed" opt;
do
  case ${opt} in
    e)
      FUNCTION="encrypt"
      echo "Encrypting file ${PLAIN_FILE}"
      break
      ;;
    d)
      FUNCTION="decrypt"
      echo "Decrypting file ${CIPHER_FILE}"
      break
      ;;
    *)
      echo "Invalid option: $OPTARG"
      usage
      exit 1
      ;;
  esac
done

if [ -z "$FUNCTION" ] ; then
  echo "Invalid encrypt/decrypt options: ${*}"
  usage
  exit 1
fi

gcloud kms "$FUNCTION" \
  --project="$PROJECT"\
  --location="global" \
  --keyring="$KEYRING" \
  --key="$KEY" \
  --plaintext-file="$PLAIN_FILE" \
  --ciphertext-file="$CIPHER_FILE" \
  && printf "\n${FUNCTION} successfully done for ${PLAIN_FILE}\n"

