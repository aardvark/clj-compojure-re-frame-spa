(ns request-server-frontend.core
  (:require
   [ajax.core :as ajax]
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [request-server-frontend.events :as events]
   [request-server-frontend.views :as views]
   [request-server-frontend.config :as config]
   [request-server-frontend.routes :as routes]
   ))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (routes/init-routes!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [routes/router-component{:router routes/router}]
                 root-el)))

(defn init []
  (ajax/GET "/requests"
    {:response-format :transit
     :handler #(re-frame/dispatch-sync [::events/initialize-db %])})
  ;;(re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
