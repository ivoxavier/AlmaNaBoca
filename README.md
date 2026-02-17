# START THE CONTAINER

docker run --rm -it -v $(pwd):/app firebase-deployer

## LOGIN
firebase login --no-localhost

firebase init functions

firebase deploy --only functions







docker run --rm \
  -v $(pwd):/app \
  firebase-deployer \
  sh -c "cd functions && npm install && cd .. && firebase deploy --only functions --token '4/0AfrIepBHbSYLSwo8Dsl9EgJvSlUtVuT6mkOn8S3vhCKHmEf9w3rSpa97Ux7rapbGWUM5Ug'"




  docker run --rm \
  -v $(pwd):/app \
  firebase-deployer \
  sh -c "cd functions && npm install && cd .. && firebase deploy --only functions --token '4/0AfrIepDYHVOXPhmgTzOfMpC-xZh44uAnW83D54ZhBoiLy65oXO0GPwyfHEVKHvBpujOe5w'"