(ns request-server-backend.main
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [request-server-backend.core :as core]))


(defn -main 
  []
  (ring.adapter.jetty/run-jetty core/app 
                                {:port 3000
                                 :join? false}))


(comment 
  (def server (-main))
  (.stop server))