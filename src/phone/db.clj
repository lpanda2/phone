(ns phone.db
  (:require [phone.layout :refer [say]]
            [mount.core :refer [defstate]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [libphonenumber.core :refer [parse-phone example-phone]]))

(defn ->maps [csv-data]
  (map zipmap (repeat [:phone :context :caller-id]) csv-data))

(defn ->clean [m]
  (update m :phone #(-> % (parse-phone "US") second :e164)))

(defn load-data-into-db []
  (let [resource (io/resource "callerid-data.csv")
        csv (with-open [reader (io/reader resource)]
              (doall (csv/read-csv reader)))
        data (->> csv ->maps (map ->clean) (group-by :phone))]
    data))

(defstate db
  :start (load-data-into-db))
