(ns request-server-frontend.events
  (:require
   [re-frame.core :as re-frame]
   [request-server-frontend.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [request-server-frontend.effects :as effects]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced 
  [db [_ requests]]
   (db/default-db requests)))

;;
;; Routing events
;;

(re-frame/reg-event-fx
 ::navigate
 (fn [db [_ route]]
   {::effects/navigate! route}))

(re-frame/reg-event-db
  ::navigated
 (fn [db [_ new-match]]
   (assoc db :current-route new-match)))

;;
;; Data mutation requests
;;
(re-frame/reg-event-db
 ::load-requests
 (fn-traced [db [_ requests]]
   (println requests)
   (update db :requests (fn [_ n] n) requests)))


(defn generate-id
  [requests]
  ;;TODO change `(reduce max ...` logic to the setting atom on ::initialize-db step
  ;; or even move request-id to the db state
  ((fnil inc 0) (reduce max (map (comp js/parseInt :id) requests))))

(re-frame/reg-event-db
 ::add-request
 (fn-traced
  [db [_ request]]
  (let [all-requests (:requests db)
        request (assoc request :id (generate-id all-requests))]
    (update db :requests (fn [old args] (conj old args)) request))))



