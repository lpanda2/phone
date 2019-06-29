(ns phone.handler
  (:require [phone.layout :refer [say]]
            [phone.config :refer [config]]
            [mount.core :refer [defstate]]
            [compojure.core :refer [GET PUT POST DELETE defroutes]]
            [compojure.handler :as handler]
            [compojure.route :refer [resources not-found]]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(defroutes app-routes

  (GET "/" [] (response "hello"))

  (resources "/")
  (not-found "not found"))

(defn create-handler [config]
  (as-> app-routes $
    (wrap-keyword-params $)
    (wrap-params $)
    (wrap-restful-format $)
    (wrap-multipart-params $)
    (wrap-stacktrace $)))

(defstate handler
  :start (create-handler config))
