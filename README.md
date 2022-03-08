# Full stack clojure Single Page application

This is a "learning project" (WIP) for SPA using full clojure stack.
Next libraries are used as a app cornerstones:

1. re-frame for frontend matter
1. datomic as backend database
1. compojure + ring for backend routing
1. transit + muntaja for backend/frontend
1. cprop for properties binding

## Current status and problems

This project is far from complete.

### Datomic dev-local license

At this moment main problem is understanding of how to properly
work with datomic dev-local packaging for local dev and release build.

By the book of license we can't distribute datomic dev-local library code in our jar.
And given that dev-local is itself depend of guava and other java libs it is not clear
which deps should be really included. Just 'patching' this by using datomic/client-cloud
didn't work out of the box.

Due to this issue app is really only working while using dev-local and I would need
to invest more time in understanding how to do a propper artifact bundling for datomic builds.

### Single build wrapper

To run development build you need to run both "deps.edn" and "shadow-cljs" builds.
It is not a critical problem, but it blocks some code autocompletion and documentation
features as most setups only able to work with one REPL at the time.

One possible solution is to fully migrate to the Leiningen, but at the moment I quite like additional flexibility deps.edn + shadow build gives.

### Fresh start

Application isn't configured for fresh start:

1. Database schema wouldn't init itself
2. UI not configured to handle "no-data" properly
3. Backend requests can't handle "no-data" requests properly

## Development build

1. shadow-cljs watch app
1. clj -M:nrepl:dev-local:dev -m nrepl.cmdline

## Release build

Release build will:

1. pack all needed js assets to the resources directory via shadow-cljs release build
1. build uberjar using uberjar

``` shell
shadow-cljs release app
clj -M:uberjar
```

Running jar will start a jetty server on port 3000 contain all backend and frontend logic:

``` shell
java -Dconf="config.edn" -cp "request-server-frontend.jar" clojure.main -m request-server-backend.main
```

```-Dconf=``` required to bootstrap application properties, see ```dev/config.edn``` example for dev builds.
