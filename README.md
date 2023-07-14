# Webhook

Webhook that can be used to receive payment POST requests from Tebex and project update POST requests from Spiget to post as embeds into Discord via their webhook feature.

## Building
```bash
./gradlew shadowJar
```

## Running
By default, this program is expected to run behind a web proxy like nginx or caddy.
```bash
java -Xmx2G -jar webhook.jar
```