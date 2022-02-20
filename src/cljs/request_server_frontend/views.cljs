(ns request-server-frontend.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [request-server-frontend.subs :as subs]
   [request-server-frontend.events :as events]
   [clojure.string :as string]))

(defn request-table-item
  []
  (fn [{:request/keys [id title description created-by completed-by completed-date]}]
    [:tr [:td id] [:td title] [:td description]
     [:td created-by] [:td completed-by] [:td (str completed-date)]]))

(defn text-input
  [label value-atom validation-atom]
  [:label label
        [:input {:type "text" :value @value-atom
                 :on-change (fn [event] (let [new-value (.-value (.-target event))]
                                          (if (clojure.string/blank? new-value)
                                            (reset! validation-atom "Should not be empty")
                                            (reset! validation-atom ""))
                                          (reset! value-atom new-value)))}]
        [:text @validation-atom]
        [:br]])


(defn date-input
  ;;TODO Maybe remove this duplication and inject type from function call directly
  [label value-atom validation-atom]
  [:label label
   [:input {:type "date" :value @value-atom
            :on-change (fn [event] (let [new-value (.-value (.-target event))]
                                     (if (clojure.string/blank? new-value)
                                       (reset! validation-atom "Should not be empty")
                                       (reset! validation-atom ""))
                                     (reset! value-atom new-value)))}]
   [:text @validation-atom]
   [:br]])

(defn not-blank 
  [s]
  ((complement clojure.string/blank?) s))

(defn create-new-request
  []
  (let [title (reagent/atom "")
        title-validation (reagent/atom "Should not be empty")
        description (reagent/atom "")
        description-validation (reagent/atom "Should not be empty")
        created (reagent/atom "")
        created-validation (reagent/atom "Should not be empty")
        completed (reagent/atom "")
        completed-validation (reagent/atom "Should not be empty")
        completed-on (reagent/atom "")
        completed-on-validation (reagent/atom "Should not be empty")
        ]
    (fn []
      [:div
       [text-input "Title:" title title-validation]
       [text-input "Description:" description description-validation]
       [text-input "Created:" created created-validation]
       [text-input "Completed:" completed completed-validation]
       [date-input "Completed-on:" completed-on completed-on-validation]

       [:button {:disabled (not-every? string/blank? 
                                       [@title-validation @description-validation
                                        @created-validation @completed-validation
                                        @completed-on-validation]
                                   )
                 :on-click
                 #(re-frame/dispatch [::events/add-request
                                      {:title @title :description @description
                                       :created-by @created :completed-by @completed
                                       :completed-date @completed-on}])}
        "Submit request"]])))

(defn request-list
  []
  (let [requests (re-frame/subscribe [::subs/requests])]
    [:div
     [:h2]
     [:table
      [:thead
       [:tr [:th "Id"] [:th "Title"] [:th "Description"]
        [:th "Created by"] [:th "Completed by"] [:th "Completed on"]]]
      [:tbody
       (for [request @requests]
         ^{:key (:id request)} [request-table-item request])]]]))


(defn main-panel
  []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]
     [request-list]]))

