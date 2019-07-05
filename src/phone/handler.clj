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
  (-> n trim (replace-first "2B" "") (parse-phone "US")))

(defn bad-request [b]
  {:status 400
   :headers {}
   :body b})

(defn query-database [{:keys [params]}]
  (let [n (:number params)
        parsed (when n (get-number n))
        invalid? (= (first parsed) :invalid)
        results (get db (-> parsed second :e164))]
    (print n)
    (cond
      invalid? (bad-request (str "are you sure " n " is a valid phone number?"))
      results (response (assoc {} :results results))
      :else (not-found (str n " was not found in our records.")))))

(defroutes app-routes

  (GET "/" [] (response "hello"))

  (GET "/query" request (query-database request))
  (POST "/number" request (response (add-to-database request)))

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
