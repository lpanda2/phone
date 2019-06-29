(ns phone.server
  (:require [mount.core :refer [defstate] :as mount]
            [phone.handler :refer [handler]]
            [phone.config :refer [config]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))


(defn start-server [& args]
  (let [port (Integer/parseInt (or (config :port) "3000"))
        app (run-jetty handler {:port port :join? false})]
    (println "Starting app on " port)
    app))

(defstate phone-app
  :start (start-server config)
  :stop (.stop phone-app))

(defn -main [& args]
  (mount/start))
