(ns request-server-backend.config
  (:require [cprop.core :refer [load-config]]))

(def config
  (load-config))