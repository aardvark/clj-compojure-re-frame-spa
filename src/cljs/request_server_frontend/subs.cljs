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
   (let [rs (:requests db)]
     (sort-by (fn [r]
                (.getTime (new js/Date (:completed-on r))))
              rs))))