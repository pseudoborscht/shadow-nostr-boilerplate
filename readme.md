# shadow clj nostr boilerplate

Boilerplate code to write clojurescript apps with [nostr-tools](https://github.com/nbd-wtf/nostr-tools).

Uses shadows-cljs to bundle app logic.

The Closure compiler [complains](https://github.com/google/closure-compiler/issues/4078) when compiling nostr-tools, so in the meantime, it has to be externally compiled with webpack.


# Setup

Rename

```
$ find ./ -type f -exec sed -i 's/shadow-boilerplate/my-clj-nostr-project/g' {} +
$ mv src/main/shadow_boilerplate src/main/my-clj-nostr-project
```

Install dependencies

```
npm i
```

webpack
```
./node_modules/.bin/webpack --mode development
```

Watch the cljs code

```
./node_modules/.bin/shadow-cljs watch main
```

Visit [http://localhost:8000](https://github.com/nbd-wtf/nostr-tools)





# License

Have fun with this code. Do whatever you like.
