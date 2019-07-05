(ns phone.db
  (:require [phone.layout :refer [say]]
            [mount.core :refer [defstate]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [libphonenumber.core :refer [parse-phone example-phone]]))

(defn ->maps [csv-data]
  (map zipmap (repeat [:number :context :name]) csv-data))

(defn ->clean [m]
  (update m :number #(-> % (parse-phone "US") second :e164)))

(defn load-data-into-db []
  (let [resource (io/resource "callerid-data.csv")
        csv (with-open [reader (io/reader resource)]
              (doall (csv/read-csv reader)))
        data (->> csv ->maps (map ->clean) (group-by :number))]
    data))

(defstate db
  :start (load-data-into-db))

(comment
  (def resource (io/resource "callerid-data.csv"))
  (def csv (with-open [reader (io/reader resource)]
             (doall (csv/read-csv reader))))
  (def data (->> csv ->maps (map ->clean) (group-by :phone))))
