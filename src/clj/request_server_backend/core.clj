(ns request-server-backend.core
  (:require
   [clojure.java.io :as io]
   [compojure.core :as compojure :refer [GET]]
   [compojure.route :as route]

   [muuntaja.middleware :as middleware]

   [request-server-backend.db :as db]))

(defn handler [_]
  {:status 200
   :body (db/initial-load)})

(compojure/defroutes app
  (GET "/" [] (io/resource "public/index.html"))
  (GET "/requests" []  (middleware/wrap-format handler))
  (route/resources "/")   
  (route/not-found "<h1>Page not found</h1>"))


(comment 
  (in-ns 'request-server-backend.core)
  (require '[ring.adapter.jetty])

  (def server
    (ring.adapter.jetty/run-jetty app {:port 3000
                                       :join? false}))
  
  (.stop server)
  )