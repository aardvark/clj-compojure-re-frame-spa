(ns request-server-frontend.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::current-route
 (fn [db]
   (:current-route db)))

(re-frame/reg-sub
 ::requests
 (fn [db]
   (println "Triggered ::requests reg-sub")
   (let [rs (:requests db)]
     (vec (sort-by (fn [r]
                     (.getTime (new js/Date (:request/completed-date r))))
                   rs)))))