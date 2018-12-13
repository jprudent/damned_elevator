# damned_elevator
I don't really know what this will be yet

![Screenshot](visual-log/2018-12-14.png?raw=true "Screenshot")

## Run

```
clj -R:cljs -M:dev-browser
```

## Choose your platform

### Browser

```bash
> clj -R:cljs -M:dev-browser
ClojureScript 1.10.439
cljs.user=> (require 'damel.kobaian :reload)
nil
cljs.user=> (js/alert (damel.kobaian/make-sentence))
nil                      
```

### NodeJS

```bash
> clj -R:cljs -M:dev-node
ClojureScript 1.10.439
cljs.user=> (require 'damel.kobaian :reload)
nil
cljs.user=> (damel.kobaian/make-sentence)
"youtō héyai yéyō té yōhitou kaitakanōyai téyaitōhouti !"                     
```

### JVM (Java)

```bash
> clj -R:clj -M:dev-jvm
Clojure 1.9.0
user=> (require 'damel.kobaian :reload)
nil
user=> (damel.kobaian/make-sentence)
"tai némaikōhō kaniyi kamōyō ?"
```

### Unit Tests on JVM

```
clj -Atest-clj -R:clj
```

## An experiment

A work I write a lot of tests.
Here I won't write ANY, FOR THE SAKE OF THE EXPERIENCE, EEEEEEEEEEEEEEEEEEEEEEEE.
