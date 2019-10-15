## LINE Bot Router

This application can accept LINE Bot webhook HTTP requests, check signature and reroute request to other bot servers.

Each downstream bot will receive each message, but only one can use reply API. Though any bot can use push APIs.

## Usage

1. Specify `channel-secret` and `channel-access-token` settings in `application.yml`.
These values can be found in the LINE Developers Console.

2. Make sure all downstream bot use the same `channel-secret` and `channel-access-token`.

3. Add all downstream bot webhooks URIs to the `downstreams` list in in `application.yml`.

4. In LINE Developers Console set `Webhook URL` to the `https://your-host.com/webhook/bot`.

5. Build `./gradlew build`.

6. Run `java $JAVA_OPTS -jar router-server/build/libs/router-server.jar --armeria.ports\[0\].port=8080 --armeria.ports\[0\].protocol=HTTP`.

## Rate limit

Rate limits for replies, push messages and other APIs are shared between all bots.
