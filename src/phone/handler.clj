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
            [ring.util.response :refer [resource-response response created]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [ring.middleware.stacktrace :refer [wrap-stacktrace]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]))

(def current-db (atom db))

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
        results (get @current-db (-> parsed second :e164))]
    (cond
      (not n) (bad-request (str "did you format the query correctly?"))
      invalid? (bad-request (str "are you sure " n " is a valid phone number?"))
      results (response (assoc {} :results results))
      :else (not-found (str n " was not found in our records.")))))

(def any? (comp boolean some))

(defn add-to-database [{:keys [params]}]
  (let [{:keys [name number context]} params
        missing-params? (any? nil? [name number context])
        parsed (when number (get-number number))
        invalid? (= (first parsed) :invalid)
        phone-number (-> parsed second :e164)
        results (get @current-db phone-number)
        context-exists? (some
                         (-> context hash-set)
                         (->> results (mapv :context)))]
    (cond
      missing-params? (bad-request (str "did you format the query correctly? are you missing a parameter?"))
      invalid? (bad-request (str "are you sure " number " is a valid phone number?"))
      context-exists? (bad-request (str "the context you provided for this phone number already exists."))
      (and results (not context-exists?)) (do
                                            (swap! current-db update phone-number conj params)
                                            (created "/number" (last (get @current-db phone-number))))
      (not results) (do
                      (swap! current-db assoc phone-number [params])
                      (created "/number" (get @current-db phone-number))))))

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
