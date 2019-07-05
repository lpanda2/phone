(defproject phone "0.1.0-SNAPSHOT"
  :description "an app that tells you whos calling"
  :url "http://localhost/phone"

  :source-paths ["src" "src/phone"]

  :dependencies [[org.clojure/clojurescript "1.10.238"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/data.csv "0.1.4"]
                 [compojure "1.6.1"]
                 [yogthos/config "0.8"]
                 [ring "1.7.0"]
                 [ring-middleware-format "0.7.4"]
                 [mount "0.1.12"]
                 [cheshire "5.8.1"]
                 [clj-http "3.9.0"]
                 [me.vlobanov/libphonenumber "0.2.0"]
                 [hiccup "1.0.5"]]

  :plugins [[lein-ring "0.8.13"]
            [lein-cljsbuild "1.1.1"]]

  :hooks [leiningen.cljsbuild]

  ; :repl-options {:init-ns phone}

  :cljsbuild {
    :builds [{:source-paths ["src/phone/cljs"]
              :compiler {:output-to "resources/public/js/phone.js"
                         :optimizations :whitespace
                         :pretty-print true
                         ;; :source-map "resources/public/js/phone.js.map"
                         }}]}

  :profiles
    {:dev {:dependencies [[ring-mock "0.1.5"]
                          [javax.servlet/servlet-api "2.5"]]
           :plugins [[lein-bin "0.3.4"]
                     [cider/cider-nrepl "0.21.2-SNAPSHOT"]]

           :resource-paths ["config/dev"]}

     :uberjar {:source-paths  ["env/prod/clj"]
               :omit-source true
               :main phone.server
               :aot [phone.server]
               :uberjar-name "phone.jar"
               :resource-paths ["config/prod"]}})
