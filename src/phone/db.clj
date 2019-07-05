(ns phone.db
  (:require [phone.layout :refer [say]]
            [mount.core :refer [defstate]]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defonce db {})


(defn csv-data->maps [csv-data]
  (map zipmap (repeat [:phone :context :caller-id]) csv-data))



(defn load-data-into-db []
  (let [resource (io/resource "callerid-data.csv")
        csv (with-open [reader (io/reader resource)]
              (doall (csv/read-csv reader)))
        data (csv-data->maps csv)]

    ))

(defstate db
  :start (load-data-into-db))
