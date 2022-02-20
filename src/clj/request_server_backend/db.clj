(ns request-server-backend.db
  (:require [datomic.client.api :as d]))


;;{:id "1" :title "Request 1 need something" :description "Request description somewhat long"
;; :created "Person A" :completed "Person B" :completed-on "2022-02-10"}
(def request-schema
  [{:db/ident :request/id
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one
    :db/doc "The id of the request"}

   {:db/ident :request/title
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The title of the request"}

   {:db/ident :request/description
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The description part of the request"}

   {:db/ident :request/created-by
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The person who created request"}

   {:db/ident :request/completed-by
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc "The person who completed request"}

   {:db/ident :request/completed-date
    :db/valueType :db.type/instant
    :db/cardinality :db.cardinality/one
    :db/doc "The UTC0 instant tiems stamp when request is completed"}])

(def request-sample-data
  [{:request/id 1 :request/title "Request 1 need something"
    :request/description "Request description somewhat long"
    :request/created-by "Person A" :request/completed-by "Person B"
    :request/completed-date #inst "2022-02-10T00:00:00.000+00:00"}
   {:request/id 13 :request/title "Request 13 need other stuff"
    :request/description "Request description really stuff words"
    :request/created-by "Person B" :request/completed-by "Person A"
    :request/completed-date #inst "2022-02-09T00:00:00.000+00:00"}])

(def pull-by-completed-date
  {:index :avet
   :selector ['*]
   :start [:request/completed-date]})

(defn client
  ([]
   (d/client {:server-type :dev-local
              :storage-dir (.getFile (clojure.java.io/resource "./../resources/"))
              :system "requests-dev"}))
  ([attrs]
   (d/client attrs)))

(defn- instant->ymd
  [instant]
  (.toString
   (.toLocalDate
    (java.time.LocalDateTime/ofInstant
     (java.time.Instant/ofEpochMilli
      (.getTime instant))
     (java.time.ZoneId/of "UTC+0")))))

(defn initial-load
  ([]
   (map  #(update % :request/completed-date instant->ymd) (initial-load (client))))
  ([client]
   (let [conn (d/connect client {:db-name "requests"})
         db (d/db conn)]
     (d/index-pull db pull-by-completed-date))))

(comment
  (initial-load (client))
  (initial-load)
  (map  #(update % :request/completed-date instant->ymd)
        (initial-load)))

(comment
  ;; init test request data
  (require '[datomic.client.api :as d])

  (def client (d/client {:server-type :dev-local
                         :storage-dir (.getFile (clojure.java.io/resource "./../resources/"))
                         :system "requests-dev"}))

  (d/create-database client {:db-name "requests"})
  (def conn (d/connect client {:db-name "requests"}))
  (d/transact conn {:tx-data request-schema})
  (d/transact conn {:tx-data request-sample-data})

  (def db (d/db conn))
  (d/q all-requests-id db)

  ;; use this to pull data for inital db population
  (d/index-pull db pull-by-completed-date))


(comment
  ;; dev-local tutorial data
  (require '[datomic.client.api :as d])
  (def client (d/client {:server-type :dev-local
                         :system "dev"}))

  (d/create-database client {:db-name "movies"})
  (def conn (d/connect client {:db-name "movies"}))

  (def movie-schema [{:db/ident :movie/title
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The title of the movie"}

                     {:db/ident :movie/genre
                      :db/valueType :db.type/string
                      :db/cardinality :db.cardinality/one
                      :db/doc "The genre of the movie"}

                     {:db/ident :movie/release-year
                      :db/valueType :db.type/long
                      :db/cardinality :db.cardinality/one
                      :db/doc "The year the movie was released in theaters"}])

  (d/transact conn {:tx-data movie-schema})

  (def first-movies [{:movie/title "The Goonies"
                      :movie/genre "action/adventure"
                      :movie/release-year 1985}
                     {:movie/title "Commando"
                      :movie/genre "thriller/action"
                      :movie/release-year 1985}
                     {:movie/title "Repo Man"
                      :movie/genre "punk dystopia"
                      :movie/release-year 1984}])

  (d/transact conn {:tx-data first-movies})

  (def db (d/db conn))

  (def all-titles-q '[:find ?movie-title
                      :where [_ :movie/title ?movie-title]])

  (d/q all-titles-q db))