(ns request-server-backend.core
  (:require
   [clojure.instant]
   [clojure.java.io :as io]
   [compojure.core :as compojure :refer [GET POST]]
   [compojure.route :as route]

   [muuntaja.middleware :as middleware]

   [request-server-backend.config :as config]
   [request-server-backend.db :as db]
   ))


(def db-client
  (db/client (:datomic config/config)))

(defn handler [_]
  {:status 200
   :body (db/initial-load db-client)})

(defn add-req-handler 
  [x]
  {:status 200
   :body (db/add db-client
                 (update (second (:body-params x))
                         :request/completed-date
                         #(clojure.instant/read-instant-date %)))})

(compojure/defroutes app
  (GET "/" [] (io/resource "public/index.html"))
  (GET "/requests" []  (middleware/wrap-format handler))
  (POST "/request" [] (middleware/wrap-format add-req-handler))
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