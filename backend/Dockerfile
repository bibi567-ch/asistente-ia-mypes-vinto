FROM node:24-alpine AS build

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY tsconfig.json tsconfig.test.json ./
COPY src ./src
COPY db ./db
RUN npm run build

FROM node:24-alpine

ENV NODE_ENV=production
ENV HOST=0.0.0.0
ENV PORT=3000
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --omit=dev
COPY --from=build /app/dist ./dist
COPY --from=build /app/db ./db
EXPOSE 3000
CMD ["sh", "-c", "node dist/db/migrate.js && node dist/server.js"]
