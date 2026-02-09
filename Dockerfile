# Usamos uma imagem leve do Node.js (versão 18 que é compatível com Firebase)
FROM node:22-alpine

# Instalamos o Firebase CLI globalmente dentro do contentor
RUN npm install -g firebase-tools

# Definimos a pasta de trabalho dentro do contentor
WORKDIR /app

# O comando padrão deixa o contentor à espera de ordens
CMD ["sh"]