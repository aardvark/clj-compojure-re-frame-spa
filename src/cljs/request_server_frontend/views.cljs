(ns request-server-frontend.views
  (:require
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [request-server-frontend.subs :as subs]
   [request-server-frontend.events :as events]
   [clojure.string :as string]
   [re-com.core :as re-com]
   [cljs-time.format :as time]
   ))

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


(defn validate-on-change
  [value tooltip icon new-value]
  (if (clojure.string/blank? new-value)
    (do
      (reset! tooltip "Should not be empty")
      (reset! icon :warning))
    (do
      (reset! tooltip "")
      (reset! icon nil)))
  (reset! value new-value))

(defn create-new-request
  []
  (let [title (reagent/atom "")
        title-validation-tooltip (reagent/atom "Should not be empty")
        title-validation-icon (reagent/atom :warning)
        description (reagent/atom "")
        description-validation-tooltip (reagent/atom "Should not be empty")
        description-validation-icon (reagent/atom :warning)
        title-validation-icon (reagent/atom :warning)
        created (reagent/atom "")
        created-validation-tooltip (reagent/atom "Should not be empty")
        created-validation-icon (reagent/atom :warning)
        completed (reagent/atom "")
        completed-validation-tooltip (reagent/atom "Should not be empty")
        completed-validation-icon (reagent/atom :warning)
        completed-on (reagent/atom nil)
        completed-on-validation (reagent/atom "Should not be empty")
        ]
    (fn []
      [:div
       [re-com/label :label "Title:"]
       [re-com/input-text
        :placeholder "Enter request title"
        :model title
        :status-icon? true
        :status @title-validation-icon
        :status-tooltip @title-validation-tooltip
        :change-on-blur? false
        :on-change (partial validate-on-change title title-validation-tooltip title-validation-icon)
        :width "320px"]

       [re-com/label :label "Description:"]
       [re-com/input-textarea
        :placeholder "Provide description of request"
        :model description
        :width "320px"
        :status-icon? true
        :status @description-validation-icon
        :status-tooltip @description-validation-tooltip
        :change-on-blur? false
        :on-change (partial validate-on-change description description-validation-tooltip description-validation-icon)]

       [re-com/label :label "Request is raised by:"]
       [re-com/input-text
        :placeholder "Enter a person who raised request"
        :model created
        :status-icon? true
        :status @created-validation-icon
        :status-tooltip @created-validation-tooltip
        :change-on-blur? false
        :on-change (partial validate-on-change created created-validation-tooltip created-validation-icon)
        :width "320px"]

       [re-com/label :label "Request is completed by:"]
       [re-com/input-text
        :placeholder "Enter a person who completed request"
        :model completed
        :status-icon? true
        :status @completed-validation-icon
        :status-tooltip @completed-validation-tooltip
        :change-on-blur? false
        :on-change (partial validate-on-change completed completed-validation-tooltip completed-validation-icon)
        :width "320px"]

       [re-com/label :label "This request was completed on date:"]
       [re-com/label :label  ((time/formatter "yyyy-MM-dd") @completed-on)]
       [re-com/datepicker
        :model completed-on
        :show-today? true
        :on-change #(reset! completed-on %)]
       ]
      

      ;; [:div
      ;;  [text-input "Title:" title title-validation]
      ;;  [text-input "Description:" description description-validation]
      ;;  [text-input "Created:" created created-validation]
      ;;  [text-input "Completed:" completed completed-validation]
      ;;  [date-input "Completed-on:" completed-on completed-on-validation]

      ;;  [:button {:disabled (not-every? string/blank? 
      ;;                                  [@title-validation @description-validation
      ;;                                   @created-validation @completed-validation
      ;;                                   @completed-on-validation]
      ;;                              )
      ;;            :on-click
      ;;            #(re-frame/dispatch [::events/add-request
      ;;                                 #:request{:title @title :description @description
      ;;                                  :created-by @created :completed-by @completed
      ;;                                  :completed-date @completed-on}])}
      ;;   "Submit request"]]
      
      
      )))

(defn request-list
  []
  (let [requests (re-frame/subscribe [::subs/requests])]
    [re-com/simple-v-table

     :model requests
     :columns [{:id :request/id :header-label "Id" :row-label-fn :request/id :width 100 :height 100}
               {:id :request/title :header-label "Title" :row-label-fn :request/title :width 250 :height 100}
               {:id :request/description :header-label "Description" :row-label-fn :request/description :width 250 :height 100}
               {:id :request/created-by :header-label "Raised by" :row-label-fn :request/created-by :width 100 :height 100}
               {:id :request/completed-by :header-label "Closed by" :row-label-fn :request/completed-by :width 120 :height 100}
               {:id :request/completed-date :header-label "Closed date" :row-label-fn :request/completed-date :width 125 :height 100}]]))


(defn main-panel
  []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1
      "Hello from " @name]
     [request-list]]))

