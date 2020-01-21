(defproject crux-http-async-client "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"]
                 [funcool/promesa "2.0.1"]
                 [funcool/httpurr "1.1.0"]
                 [org.clojure/core.async "0.6.532"]
                 [juxt/crux-core "19.12-1.6.1-alpha"]]
  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-doo "0.1.11"]]
  :doo {:paths {:rhino "lein run -m org.mozilla.javascript.tools.shell.Main"}}
  :aliases {"test-cljs" ["with-profile" "test" "doo" "node" "node-test" "once"]
            "test-all"  ["do" ["test"] ["test-cljs"]]}
  :profiles
  {:test {:dependencies [[org.mozilla/rhino "1.7.7"]]
          :cljsbuild
          {:builds
           {:node-test {:source-paths ["src" "test"]
               :compiler {:output-to "target/testable.js"
                          :output-dir "target"
                          :main crux-http-async-client.test-runner
                          :target :nodejs}}
            }}}
   })
:test
            (comment {:source-paths ["src" "test"]
             :compiler {:output-to "target/main.js"
                        :output-dir "target"
                        :main crux-http-async-client.test-runner
                        :optimizations :simple}})

