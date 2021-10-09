(defproject pingpong "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.879"]
                 [thheller/shadow-cljs "2.15.10"]
                 [com.google.javascript/closure-compiler-unshaded "v20210505"]
                 [ring/ring-core "1.7.1"]
                 [io.pedestal/pedestal.log "0.5.8"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [io.pedestal/pedestal.service "0.5.5"]
                 [io.pedestal/pedestal.service-tools "0.5.5"]
                 [io.pedestal/pedestal.jetty "0.5.5"]
                 [metosin/ring-http-response "0.9.1"]
                 [cheshire "5.10.0"]

                 [day8.re-frame/tracing "0.6.2"]
                 [day8.re-frame/re-frame-10x "1.0.2"]

                 [reagent "1.0.0"]
                 [re-frame "1.2.0"]
                 [day8.re-frame/http-fx "0.2.3"]]
  :source-paths ["src/clj" "src/cljc" "src/cljs"]

  :aliases {"watch-cljs" ["run" "-m" "shadow.cljs.devtools.cli" "watch" "client"]}

  :test-paths ["test/clj" "test/cljc"]
  :clean-targets ^{:protect false} ["resources/public/js" "target/js"]
  :main ^:skip-aot pingpong.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
