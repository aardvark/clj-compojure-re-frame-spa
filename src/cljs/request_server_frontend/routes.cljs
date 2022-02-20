(ns request-server-frontend.routes
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]

   [reitit.core :as r]
   [reitit.coercion :as rc]
   [reitit.coercion.spec :as rss]
   [reitit.frontend :as rf]
   [reitit.frontend.controllers :as rfc]
   [reitit.frontend.easy :as rfe]

   [request-server-frontend.events :as events]
   [request-server-frontend.views :as views]
   [request-server-frontend.subs :as subs]))

(defn href
  "Return relative url for given route. Url can be used in HTML links."
  ([k]
   (href k nil nil))
  ([k params]
   (href k params nil))
  ([k params query]
   (rfe/href k params query)))

(def routes
  ["/"
   [""
    {:name      ::list
     :view      views/request-list
     :link-text "List of requests"
     :controllers
     [{;; Do whatever initialization needed for home page
       :start (fn [& params] (js/console.log "Entering list page"))
       ;; Teardown can be done here.
       :stop  (fn [& params] (js/console.log "Leaving list page"))}]}]
   ["create"
    {:name      ::create
     :view      views/create-new-request
     :link-text "Create new request"
     :controllers
     [{:start (fn [& params] (js/console.log "Entering create page"))
       :stop  (fn [& params] (js/console.log "Leaving create page"))}]}]])

(defn on-navigate [new-match]
  (let [old-match (re-frame/subscribe [::subs/current-route])]
    (when new-match
      (let [cs (rfc/apply-controllers (:controllers @old-match) new-match)
            m  (assoc new-match :controllers cs)]
        (re-frame/dispatch [::events/navigated m])))))

(def router
  (rf/router
   routes
   {:data {:coercion rss/coercion}}))

(defn init-routes! []
  (js/console.log "initializing routes")
  (rfe/start!
   router
   on-navigate
   {:use-fragment true}))

(defn nav [{:keys [router current-route]}]
  (into
   [:ul]
   (for [route-name (r/route-names router)
         :let       [route (r/match-by-name router route-name)
                     text (-> route :data :link-text)]]
     [:li
      (when (= route-name (-> current-route :data :name))
        "> ")
      ;; Create a normal links that user can click
      [:a {:href (href route-name)} text]])))

(defn router-component [{:keys [router]}]
  (let [current-route @(re-frame/subscribe [::subs/current-route])]
    [:div
     [nav {:router router :current-route current-route}]
     (when current-route
       [(-> current-route :data :view)])]))