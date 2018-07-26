(defproject gm-engine "1.0.0"
  :description "Portable engine for processing dice roll strings"
  :url "https://github.com/TuesdayHat/gm-engine"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot gm-engine.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
