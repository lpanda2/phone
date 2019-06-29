(ns phone.config
  (:require [config.core :refer [env]]
            [mount.core :as mount :refer [defstate]]))

(defn load-config [] env)

(defstate config
  :start (load-config))
