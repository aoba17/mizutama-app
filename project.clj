(defproject mizutama-app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520"]
                 [binaryage/oops "0.7.0"]
                 [reagent "0.8.1" :exclusions [cljsjs/react cljsjs/react-dom cljsjs/react-dom-server cljsjs/create-react-class]]
                 [re-frame "0.10.8"]
                 [funcool/promesa "4.0.2"]]
  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-figwheel "0.5.19"]]
  :clean-targets ["target/" "main.js"]
  :aliases {"figwheel"        ["run" "-m" "user" "--figwheel"]
                                        ; TODO: Remove custom extern inference as it's unreliable
                                        ;"externs"         ["do" "clean"
                                        ;                   ["run" "-m" "externs"]]
            "rebuild-modules" ["run" "-m" "user" "--rebuild-modules"]
            "prod-build"      ^{:doc "Recompile code with prod profile."}
            ["with-profile" "prod" "cljsbuild" "once" "main"]}
  :profiles {:dev  {:dependencies [[figwheel-sidecar "0.5.18"]
                                   [cider/piggieback "0.4.1"]]
                    :source-paths ["src" "env/dev"]
                    :cljsbuild    {:builds [{:id           "main"
                                             :source-paths ["src" "env/dev"]
                                             :figwheel     true
                                             :compiler     {:output-to     "target/expo/index.js"
                                                            :main          "env.expo.main"
                                                            :output-dir    "target/expo"
                                                            :optimizations :none
                                                            :target        :nodejs}}]}
                    :repl-options {:nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}
             :prod {:cljsbuild {:builds [{:id           "main"
                                          :source-paths ["src" "env/prod"]
                                          :compiler     {:output-to          "main.js"
                                                         :main               "env.expo.main"
                                                         :output-dir         "target/expo"
                                                         :static-fns         true
                                                         :externs            ["js/externs.js"]
                                                         :infer-externs      true
                                                         :parallel-build     true
                                                         :optimize-constants true
                                                         :optimizations      :advanced
                                                         :closure-defines    {"goog.DEBUG" false}
                                                         :target             :nodejs}}]}}})
