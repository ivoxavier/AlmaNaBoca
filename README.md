# START THE CONTAINER

docker run --rm -it -v $(pwd):/app firebase-deployer

## LOGIN
firebase login --no-localhost

firebase init functions

firebase deploy --only functions