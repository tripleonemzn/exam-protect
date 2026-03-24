FROM golang:1.21-alpine AS build
WORKDIR /src
COPY . .
RUN go build -o /bin/server ./cmd/server

FROM alpine:3.19
WORKDIR /app
COPY --from=build /bin/server /app/server
EXPOSE 8080
ENTRYPOINT ["/app/server"]
