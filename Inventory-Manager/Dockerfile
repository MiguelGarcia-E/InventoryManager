# Etapa 1: build de la app (Vite)
FROM node:20-alpine AS build

WORKDIR /app

# Copiamos solo lo necesario para instalar deps
COPY package*.json ./
RUN npm install

# Copiamos el resto del código
COPY . .

# Build de producción -> genera carpeta /dist
RUN npm run build

# Etapa 2: Nginx sirviendo el estático
FROM nginx:alpine

# Reemplazamos la config por nuestro nginx.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copiamos el build de Vite a la carpeta que sirve Nginx
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
