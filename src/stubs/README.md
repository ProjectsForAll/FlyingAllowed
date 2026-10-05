# Compile-only API stubs

Signature-only copies of third-party plugin APIs that are not published to any
reachable Maven repository. They are compiled into the `stubs` source set, put
on the main source set's `compileOnly` classpath, and never packaged: at runtime
the real classes come from the plugin itself.

Each stub must match the real class's package, name, and member signatures
exactly, or the hook will throw `NoSuchMethodError` on a live server.

- `com/craftaro/skyblock/api` — FabledSkyBlock 3.x, from
  https://github.com/Songoda-Plugins/FabledSkyBlock (`src/main/java/com/craftaro/skyblock/api`).
