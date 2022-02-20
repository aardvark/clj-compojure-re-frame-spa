(ns request-server-frontend.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [request-server-frontend.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
