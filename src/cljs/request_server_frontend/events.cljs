(ns request-server-frontend.events
  (:require
   [ajax.core :as ajax]
   [re-frame.core :as re-frame]
   [request-server-frontend.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [day8.re-frame.http-fx]

   [request-server-frontend.effects :as effects]))

(re-frame/reg-event-fx
 ::initial-load
 (fn-traced
  [{:keys [db]} [_ _]]
  {:db db/default-db
   :fx [[:dispatch [::initial-load-requests]]]}))

(re-frame/reg-event-fx
 ::initial-load-requests
 (fn-traced
  [{:keys [db]} _]
  {:db db
   :http-xhrio {:method :get
                :uri "/requests"
                :timeout 7000
                :response-format (ajax/transit-response-format)
                :on-success [::initial-load-requests-success]
                :on-failure [::initial-load-requests-failure]}}))

(re-frame/reg-event-db
 ::initial-load-requests-success
 (fn [db [_ regs]]
   (assoc db :requests regs)))

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
  ((fnil inc 0) (reduce max (map (comp js/parseInt :request/id) requests))))

(re-frame/reg-event-fx
 ::add-request
 (fn-traced
  [{:keys [db]} [_ request]]
  (let [all-requests (:requests db)
        request (assoc request :request/id (generate-id all-requests))]
    {:db (update db :requests (fn [old args] (conj old args)) request)
     :fx [[:dispatch [::navigate :request-server-frontend.routes/list]]]})))



