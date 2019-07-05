(ns phone.handler
  (:require [phone.layout :refer [say]]
            [phone.config :refer [config]]
            [phone.db :refer [db]]
            [mount.core :refer [defstate]]
            [libphonenumber.core :refer [parse-phone]]
            [clojure.string :refer [trim replace-first]]
            [compojure.core :refer [GET PUT POST DELETE defroutes]]
            [compojure.handler :as handler]
            [compojure.route :refer [resources not-found]]
            [ring.util.response :refer [resource-response response]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(defn get-number [n]
  (get db (-> n trim (replace-first "2B" "") (parse-phone "US"))))

(defn bad-request [b]
  {:status 400
   :headers {}
   :body b})

(defn query-database [n]
  (let [db-res (get-number n)
        status (first db-res)
        res (-> db-res second :e164)]
    (cond
      (= status :invalid) (bad-request (str "are you sure " n " is a valid phone number?"))
      res (response (assoc {} :results res))
      :else (not-found (str n " was not found in our records.")))))

(defroutes app-routes

  (GET "/" [] (response "hello"))

  (GET "/query" [number] (query-database number))

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
