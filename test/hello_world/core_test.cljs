(ns hello-world.core-test
  (:require [clojure.test :refer :all])
  (:require [hello-world.core :refer :all]))


(deftest foo-test
  (testing "math"
    (is (= 1 (+ 1 1)))))
