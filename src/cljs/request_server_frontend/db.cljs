(ns request-server-frontend.db)


(defn default-db 
  [requests]
  {:name "server"
   :current-route nil
   :requests requests})