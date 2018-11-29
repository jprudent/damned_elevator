(ns damel.elevator-test
  (:require [clojure.test :refer :all]
            [damel.elevator :as sut]))

#_(deftest loop-test
  (testing "elevator can move"
    (is (= 1 1))
    #_  (is (= {:position     1
            :speed        1
            :acceleration 1
            :force        1
            :mass         1}
           (as-> (sut/->MotorPulled 1) %
                 (sut/add-event sut/world-0 %)
                 (sut/game-loop % 1)
                 (:elevator %))))))
