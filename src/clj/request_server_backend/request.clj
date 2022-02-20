(ns request-server-backend.request)

(defn all 
  []
  [{:id "1" :title "Request 1 need something from server" :description "Request description somewhat long"
    :created "Person A" :completed "Person B" :completed-on "2022-02-10"}
   {:id "2" :title "Request 2 need something" :description "Request description somewhat different"
    :created "Person C" :completed "Person D" :completed-on "2022-02-07"}])